# Introduction to Document Databases and MongoDB

This application illustrates how to use MongoDB from a Java program.

## Tutorial

It might be a good idea to learn the basics of the MongoDB Java driver by following the tutorial at https://developer.mongodb.com/quickstart/java-setup-crud-operations

## How to Execute

1. Clone this git repository
1. Change to the newly created directory `cd doc-db-intro`
1. This application uses a free cluster in MongoDB Atlas. Hot to create such a cluster is explained here https://developer.mongodb.com/quickstart/free-atlas-cluster
1. You have to set the following environment variables before starting the program.
    * `WEATHERDB_SERVER` The url to which the MongoDB driver will connect. How to find the URL is explained in the Atlas quickstart guide mentioned above.
    * `OPENWEATHERMAP_KEY` The API key for https://www.openweathermap.org/ from where weather observations are loaded. You must register to get a key, but registering is free.
    * `WEATHERBIT_KEY` The API key for https://www.weatherbit.io/ from where weather observations are loaded. You must register to get a key, but registering is free.
1. Build the project with the command mvn install
1. Run the program with the command mvn exec:java
