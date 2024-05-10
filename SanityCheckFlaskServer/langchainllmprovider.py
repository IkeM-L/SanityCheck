import json
from llm_provider import LLMProvider
import langchain
from langchain_community.llms import Ollama
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_openai import ChatOpenAI

# Get the API key from the config file
with open('config.json', 'r') as config_file:
    config = json.load(config_file)
    openAI_api_key = config

with open('prompt.txt', 'r') as config_file:
    system_prompt = config_file.read()

class LangchainLlmProvider(LLMProvider):
    def __init__(self):
        pass

    def ask(self, prompt, model):
        llm = Ollama(model=model)
        output_parser = StrOutputParser()
        chain = llm | output_parser
        return llm.invoke([
            "system", system_prompt,
            "user", prompt
        ])


# Try the langchain_llm_provider
# llm_provider = LangchainLlmProvider()
# response = llm_provider.ask("What is the meaning of life?", "llama3")
# print(response)
