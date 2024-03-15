import streamlit as st
from pymongo import MongoClient
import json
from value_generator import generate_value
from constants import limit_docs_insertion, batch_size


def create_indexes(collection, indexes):
    for index in indexes:
        field = index["field"]
        index_type = str(index.get("type", "ascending")).lower()
        unique = bool(index.get("unique", False))

        index_spec = [(field, 1 if index_type == "ascending" else -1)]
        collection.create_index(index_spec, unique=unique)

    st.success("Indexes created successfully.")


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


def data_generator_tab(database_name_env, collection_name_env, mongodb_url):
    connection_string = st.text_input('MongoDB Connection String', value=mongodb_url, key="conn_str1")
    database_name = st.text_input('Database Name', value=database_name_env, key="db_name1")
    collection_name = st.text_input('Collection Name', value=collection_name_env, key="coll_name1")

    # Parse JSON template and rest of the logic from the original Tab 1
    # For demonstration, just a placeholder for the actual functionality
    col_data_template, col_index_template = st.columns(2)

    with col_data_template:
        data_template_input = st.text_area('Data Template', '{}', height=250, key="data_template")
    with col_index_template:
        index_template_input = st.text_area('Index Template', '[{}]', height=250, key="index_template")

    num_docs = st.number_input('Number of Documents to Generate', min_value=1, max_value=limit_docs_insertion, value=1)

    # Parse the JSON template
    try:
        data_template = json.loads(data_template_input)
        index_template = json.loads(index_template_input)
    except json.JSONDecodeError:
        st.error('Invalid JSON template')
        st.stop()

    # Buttons for generating and inserting data
    if st.button('Generate Sample Data'):
        sample_data = generate_documents(data_template, 1)
        st.json(sample_data)

    if st.button('Create Index'):
        if connection_string and collection_name and database_name:
            client = MongoClient(connection_string)
            db = client[database_name]
            collection = db[collection_name]
            create_indexes(collection, index_template)
        else:
            st.error('Input connection string collection name and database')

    if st.button('Insert Data into MongoDB'):
        if connection_string and collection_name and database_name:
            client = MongoClient(connection_string)
            db = client[database_name]
            collection = db[collection_name]
            if num_docs <= limit_docs_insertion:
                documents = generate_documents(data_template, num_docs)

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
