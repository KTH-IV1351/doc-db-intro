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

package se.kth.iv1351;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final String DB_NAME = "sample_training";
    private static final String COLLECTION_NAME = "grades";
    private static final int SEARCH_EX_FIELD_VALUE = 5;
    private static final String SEARCH_EX_FIELD_NAME = "student_id";

    public static void main(String[] args) {
        String dbUrl = System.getenv("MONGODB_URL");
        try (MongoClient connection = MongoClients.create(dbUrl)) {

            LOGGER.trace("Databases:");
            for (Document database : connection.listDatabases()) {
                LOGGER.trace(database.toJson());
            }
            LOGGER.trace("\n");

            LOGGER.trace("Collections in " + DB_NAME + ":");
            MongoDatabase trainingDb = connection.getDatabase(DB_NAME);
            for (Document collection : trainingDb.listCollections()) {
                LOGGER.trace(collection.toJson());
            }
            LOGGER.trace("\n");

            LOGGER.trace("Documents in " + COLLECTION_NAME + " for " + SEARCH_EX_FIELD_NAME 
                         + " = " + SEARCH_EX_FIELD_VALUE + ":");
            MongoCollection<Document> grades = trainingDb.getCollection(COLLECTION_NAME);
            for (Document doc : grades.find(eq(SEARCH_EX_FIELD_NAME, SEARCH_EX_FIELD_VALUE))) {
                LOGGER.trace(doc.toJson());
            }
            LOGGER.trace("\n");
        }
    }
}
