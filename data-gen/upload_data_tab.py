import streamlit as st
import json
from pymongo import MongoClient



def upload_data(database_name, collection_name, mongodb_url):
    st.text_input('MongoDB Connection String', value=mongodb_url, key="conn_str2")
    st.text_input('Database Name', value=database_name, key="db_name2")
    st.text_input('Collection Name', value=collection_name, key="coll_name2")
    uploaded_file = st.file_uploader("Choose a JSON file", type='json', key="file_uploader")

    if uploaded_file is not None:

        data = [json.loads(line) for line in uploaded_file if line.strip()]
        # Button to insert the data into MongoDB
        if st.button('Insert Uploaded Data into MongoDB'):
            client = MongoClient(mongodb_url)
            db = client[database_name]
            collection = db[collection_name]

            # Check if data is a list of records or a single dictionary
            if isinstance(data, list):
                collection.insert_many(data)
            else:
                collection.insert_one(data)
            st.success(f'Successfully inserted data into MongoDB.')