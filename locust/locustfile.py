import os
from locust import FastHttpUser, task, between

# Get the input type from environment variables
input_type = os.getenv('INPUT_TYPE', 'aggregate')

class MyUser(FastHttpUser):

    def post_aggregate_generic(self):
        json_data = {
            "opName": os.getenv("OP_NAME"),
            "expression": os.getenv("EXPRESSION"),
            "expectedFieldName": os.getenv("RESULT_FIELD"),
            "collectionName": os.getenv("COLLECTION")
        }
        self.client.post("aggregate/generic", json=json_data)

    def post_aggregate_pipeline(self):
        json_data = {
            "opName": os.getenv("OP_NAME"),
            "expression": os.getenv("EXPRESSION"),
            "expectedFieldName": os.getenv("RESULT_FIELD"),
            "collectionName": os.getenv("COLLECTION")
        }
        self.client.post("aggregate/pipeline", json=json_data)

    @task
    def execute_task_based_on_input(self):
        if input_type == 'aggregate':
            self.post_aggregate_generic()
        elif input_type == 'pipeline':
            self.post_aggregate_pipeline()
        else:
            print(f"Unknown input_type: {input_type}")
