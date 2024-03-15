import os
import streamlit as st
from generate_data_tab import data_generator_tab
from upload_data_tab import upload_data

batch_size = 500
database_name_env = os.getenv('DATABASE','test')
collection_name_env = os.getenv('COLLECTION', 'coll')
mongodb_url = os.getenv('MONGODB_URL', 'mongodb://localhost:27017/mydatabase')



# Streamlit UI
st.title('MongoDB Synthetic Data Generator')
tab1, tab2 = st.tabs(["Generate Data", "Upload Data"])

with tab1:
# JSON template input
    data_generator_tab(database_name_env, collection_name_env, mongodb_url)

with tab2:
    upload_data(database_name_env, collection_name_env, mongodb_url)