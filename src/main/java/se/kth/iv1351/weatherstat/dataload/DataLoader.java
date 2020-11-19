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

package se.kth.iv1351.weatherstat.dataload;

import java.io.IOException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Loads data from all listed weather services. It is hardcoded which weather
 * services to use, and for which location to read observations. The data will
 * be stored exactly as it is read, by the database server specified by the url
 * in the environment variable <code>DB_SERVER</code>.
 */
public class DataLoader {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
  private static final String DB_NAME = "weatherdb";
  private static final String COLLECTION_NAME = "weather_data";
  private String[] weatherServices = {
      "http://api.openweathermap.org/data/2.5/weather?q=stockholm,se&APPID=8ef1b0765aa3ba4f779eefb389472313" };

  /**
   * Starts the program and downloads current weather observation from all
   * services. The environment variable DB_SERVER must hold a url pointing to the
   * MongoDB database server hosting the weather database.
   * 
   * @param args The application has no command line arguments.
   */
  public static void main(String[] args) {
    new DataLoader().loadCurrentObservations();
  }

  private void loadCurrentObservations() {
    OkHttpClient client = new OkHttpClient();

    for (String weatherService : weatherServices) {
      Request request = new Request.Builder().url(weatherService).build();

      try (Response response = client.newCall(request).execute()) {
        storeObservation(response.body().string());
      } catch (IOException ioe) {
        LOGGER.error("Could not load observation.", ioe);
      }
    }
  }

  private void storeObservation(String observation) {
    String weatherDbUrl = System.getenv("DB_SERVER");

    try (MongoClient connection = MongoClients.create(weatherDbUrl)) {
      MongoDatabase weatherDb = connection.getDatabase(DB_NAME);
      MongoCollection<Document> weatherColl = weatherDb.getCollection(COLLECTION_NAME);
      weatherColl.insertOne(Document.parse(observation));
    }
  }
}
