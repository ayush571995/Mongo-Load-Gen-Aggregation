import os,json
from locust import FastHttpUser, task, between


class MyUser(FastHttpUser):

    @task
    def post_task(self):
        # Reading POST parameters from environment variables
        # post_params = os.getenv("POST_PARAMS", '{"key": "default_value"}')
        json_data = {
            "opName": os.getenv("OP_NAME"),
            "expression": os.getenv("EXPRESSION"),
            "expectedFieldName": os.getenv("RESULT_FIELD"),
            "collectionName": os.getenv("COLLECTION")
        }
        # Converting the string representation of the dictionary to an actual dictionary
        # post_data = json.loads(post_params)

        self.client.post("aggregate/generic", json=json_data)
