import os

import streamlit as st
import json
from pymongo import MongoClient
from value_generator import generate_value

limit_docs_insertion = 100000
batch_size = 500
database_name = 'test'
collection_name_env = os.getenv('COLLECTION', 'coll')
mongodb_url = os.getenv('MONGODB_URL', 'mongodb://localhost:27017/mydatabase')


# Function to generate documents based on the template
def generate_documents(template, num_docs):
    documents = []
    if num_docs > limit_docs_insertion:
        st.error('hard limit set cannot be greater than 100000')
    for _ in range(num_docs):
        document = {}
        for field, data_type in template.items():
            document[field] = generate_value(data_type)
        documents.append(document)
    return documents

# Streamlit UI
st.title('MongoDB Synthetic Data Generator')

connection_string = st.text_input('MongoDB Connection String', mongodb_url)

# Database and collection name input
collection_name = st.text_input('Collection Name', collection_name_env)

# JSON template input
template_input = st.text_area('JSON Template', '{}', height=250)
num_docs = st.number_input('Number of Documents to Generate', min_value=1, max_value=limit_docs_insertion, value=1)

# Parse the JSON template
try:
    template = json.loads(template_input)
except json.JSONDecodeError:
    st.error('Invalid JSON template')
    st.stop()


# Buttons for generating and inserting data
if st.button('Generate Sample Data'):
    sample_data = generate_documents(template, 1)
    st.json(sample_data)

if st.button('Insert Data into MongoDB'):
    if connection_string and collection_name:
        client = MongoClient(connection_string)
        db = client[database_name]
        collection = db[collection_name]

        if num_docs <= limit_docs_insertion:
            documents = generate_documents(template, num_docs)

            # Create a placeholder for progress messages
            progress_message = st.empty()


            # Function to split documents into batches
            def batch(iterable, n=1):
                l = len(iterable)
                for ndx in range(0, l, n):
                    yield iterable[ndx:min(ndx + n, l)]


            # Insert documents in batches and update progress
            inserted_count = 0
            for batch_documents in batch(documents, batch_size):
                collection.insert_many(batch_documents)
                inserted_count += len(batch_documents)
                progress_message.markdown(f"Inserting documents... {inserted_count}/{num_docs}")

            # After insertion is complete, update the message to show completion
            progress_message.markdown(
                f"<span style='color:green'>Completed: {inserted_count} documents inserted successfully into {database_name}.{collection_name}.</span>",
                unsafe_allow_html=True)
        else:
            st.error("The number of documents cannot be greater than 100,000.")
    else:
        st.error('Please fill in all MongoDB connection details.')
