from flask import Flask, request, jsonify
from redis import Redis, RedisError
import json

from langchainllmprovider import LangchainLlmProvider
from llm_provider import OpenAIProvider

app = Flask(__name__)

# Load configuration
with open('config.json', 'r') as config_file:
    config = json.load(config_file)
    openAI_api_key = config['OPENAI_API_KEY']

# Initialize Redis
redis_host = 'localhost'  # Adjust as necessary
redis_port = 6379  # Default Redis port
redis_db = 0  # Default database
cache = Redis(host=redis_host, port=redis_port, db=redis_db, decode_responses=True)

# Fallback in-memory cache
fallback_cache = {}

# Cache expiration time
cache_expiration = 3600  # 1 hour in seconds


def get_cache(key):
    """Attempt to retrieve a cached item from Redis or fallback to in-memory cache."""
    try:
        result = cache.get(key)
        if result:
            return json.loads(result)
    except RedisError:
        print("Redis is unavailable, using in-memory cache")
        return fallback_cache.get(key)
    return None


def set_cache(key, value):
    """Set a cached item in Redis or fallback to in-memory cache if Redis is down."""
    try:
        cache.set(key, json.dumps(value), ex=cache_expiration)
    except RedisError:
        print("Redis is unavailable, using in-memory cache")
        fallback_cache[key] = value


num_requests = 0


@app.route('/ask', methods=['POST'])
def ask():
    """
    Ask a question to the language model about a code snippet and a comment.
    :return: The response from the language model, hopefully either NOTHING
    or a helpful comment about the code snippet and the comment.
    WARNING: Some language models are very bad at generating NOTHING exactly
    """
    data = request.get_json()
    prompt = data.get('prompt')
    code = data.get('code')
    comment = data.get('comment')
    model = data.get('model', 'gpt-3.5-turbo')  # Default model if not specified
    cache_key = f"{code}#{comment}#{model}"  # Create a unique cache key

    # Check if response is in cache
    cached_response = get_cache(cache_key)
    if cached_response:
        print("Returning cached response")
        return jsonify({'response': cached_response})

    # Not in cache, call the provider
    prompt = code + "\n" + comment
    if not prompt:
        return jsonify({'error': 'No data provided'}), 400

    # Decide the provider based on model
    if model in ['gpt-3.5-turbo', 'gpt-4-turbo-preview']:
        llm_provider = OpenAIProvider(api_key=openAI_api_key)
    else:
        llm_provider = LangchainLlmProvider()

    # Get the response from the provider
    response = llm_provider.ask(prompt, model)
    # Store the response in cache
    set_cache(cache_key, response)

    # Print the response
    print("LLM response: ", response)

    # Print how many requests have been made to the LLM
    global num_requests
    print("Number of requests: ", num_requests)
    num_requests += 1

    # Return the response
    return jsonify({'response': response})


if __name__ == '__main__':
    app.run(debug=True)
