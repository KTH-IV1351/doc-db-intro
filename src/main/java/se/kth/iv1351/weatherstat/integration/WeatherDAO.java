/*
 * The MIT License (MIT)
 * Copyright (c) 2020 Leif Lindbäck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction,including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so,subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package se.kth.iv1351.weatherstat.integration;

import java.util.List;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This data access object (DAO) encapsulates all database calls in the weather
 * application. No code outside this class shall have any knowledge about the
 * database. The URL of the MongoDB database server hosting the weather
 * database is read from the environment variable WEATHERDB_SERVER.
 */
public class WeatherDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherDAO.class);
    private static final String DB_NAME = "weatherdb";
    private static final String COLLECTION_NAME = "weather_data";
    private MongoClient connection;

    /**
     * Constructs a new DAO object connected to the weather database.
     */
    public WeatherDAO() {
        connectToWeatherDB();
    }

    /**
     * Stores all specified observations in the weather database.
     * 
     * @param observations The observations to store.
     */
    public void storeObservation(List<String> observations) {
        MongoDatabase weatherDb = connection.getDatabase(DB_NAME);
        MongoCollection<Document> weatherColl = weatherDb.getCollection(COLLECTION_NAME);
        for (String observation : observations) {
            weatherColl.insertOne(Document.parse(observation));
        }
    }

    private void connectToWeatherDB() {
        String weatherDbUrl = System.getenv("WEATHERDB_SERVER");
        connection = MongoClients.create(weatherDbUrl);
    }
}
