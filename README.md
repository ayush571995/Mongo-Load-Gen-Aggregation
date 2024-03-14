# About

* This repo is a containerised application which allows you to connect to a mongodb via a spring boot application
* You can hit the mongodb with a specific aggregation query and get response
* Also, having additional capability to generate load and put the load
* This captures your metrics around the DB call and published on dashboard in grafana.

# Run

* This is a dockerized application .
* All you need to do is get the mongodb url and update in the docker-compose file env variable.


  **_Note_**:- When adding the mongodb url make sure to add database name in the connection string in case it's not there.   

* Also, you need to identify the aggregation query expression and update in the application params
* I have already put the example of toLower aggregation operator in docker-compose file. As ,a requirement in docker-compose every dollar-sign needs to be escaped using a $ sign
* You need to have the data in place for your collection so, put the required params in docker-compose for collectionName, opName, resultField, expression.
* In case you wish to generate the data have added a data-gen module. This is a UI based document generator and you can define custom document.

## Data-Gen
* This runs as a python application based on streamlit on port 8501.
* It has support for uploading data as well.

This is a structure of json you can write inside the json editor and click on generate document. 
You can find the full list of the allowed fields at value_generator.py

```
{
"name": "t_name",
"email: "t_email",
// generating array of emails
"listEmails: "t_array:10:t_email",
"phone": "t_phone",
.. etc
}
```

# To build the docker images
docker-compose build

# To run
docker-compose up
```
* Once, started you can put load using locust . You need to go to localhost:8090 and provide the params for the load.
* Note:- For putting load on localhost you should use url : http://host.docker.internal:8080

## Metrics

* You can view metrics in grafa directly on localhost:3000
* This is your metrics around the round trip time for the application to make a call to the mongo and get the response.
* If you want e2e client side metrics you can use locust charts

