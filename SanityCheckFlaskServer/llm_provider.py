import json
from abc import ABC, abstractmethod
import openai

with open('prompt.txt', 'r') as config_file:
    system_prompt = config_file.read()


class LLMProvider(ABC):
    @abstractmethod
    def ask(self, prompt, model):
        pass


class OpenAIProvider(LLMProvider):
    def __init__(self, api_key):
        self.api_key = api_key

    def ask(self, prompt, model):
        openai.api_key = self.api_key
        response = openai.chat.completions.create(
            model=model,  # Dynamic model selection
            messages=self.construct_message(prompt),
            max_tokens=150
        )
        return response.choices[0].message.content

    @staticmethod
    def construct_message(message):
        """
        Constructs an openAI array of messages from a single message
        :param message: The message
        :return: A JSON array of messages
        """
        return [{"role": "system", "content": system_prompt},
                {"role": "user", "content": message}]