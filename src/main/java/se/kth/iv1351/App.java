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

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final String DB_NAME = "sample_training";
    private static final String COLLECTION_NAME = "grades";
    private static final String STUD_ID_FIELD_NAME = "student_id";
    private static final double STUD_ID_SEARCH_FIELD_VALUE = 5;
    private static final double STUD_ID_INSERT_FIELD_VALUE = 10000;

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
            try (MongoCursor<Document> collections = trainingDb.listCollections().iterator()) {
                while (collections.hasNext()) {
                    LOGGER.trace(collections.next().toJson());
                }
            }
            LOGGER.trace("\n");

            LOGGER.trace("Documents in " + COLLECTION_NAME + " for " + STUD_ID_FIELD_NAME + " = "
                    + STUD_ID_SEARCH_FIELD_VALUE + ":");
            MongoCollection<Document> gradesColl = trainingDb.getCollection(COLLECTION_NAME);
            try (MongoCursor<Document> docs = gradesColl.find(eq(STUD_ID_FIELD_NAME, STUD_ID_SEARCH_FIELD_VALUE))
                    .iterator()) {
                while (docs.hasNext()) {
                    LOGGER.trace(docs.next().toJson());
                }
            }
            LOGGER.trace("\n");

            LOGGER.trace("Documents in " + COLLECTION_NAME + " for " + STUD_ID_FIELD_NAME + " = "
                    + STUD_ID_INSERT_FIELD_VALUE + ":");
            gradesColl = trainingDb.getCollection(COLLECTION_NAME);
            try (MongoCursor<Document> docs = gradesColl.find(eq(STUD_ID_FIELD_NAME, STUD_ID_INSERT_FIELD_VALUE))
                    .iterator()) {
                while (docs.hasNext()) {
                    LOGGER.trace(docs.next().toJson());
                }
            }
            LOGGER.trace("\n");

            Random rand = new Random();
            Document newStud = new Document("_id", new ObjectId());
            newStud.append(STUD_ID_FIELD_NAME, STUD_ID_INSERT_FIELD_VALUE).append("class_id", 1d).append("scores",
                    Arrays.asList(new Document("type", "exam").append("score", rand.nextDouble()),
                            new Document("type", "quiz").append("score", rand.nextDouble()),
                            new Document("type", "homework").append("score", rand.nextDouble()),
                            new Document("type", "hw").append("score", rand.nextDouble())));
            gradesColl.insertOne(newStud);

            LOGGER.trace("Documents in " + COLLECTION_NAME + " for " + STUD_ID_FIELD_NAME + " = "
                    + STUD_ID_INSERT_FIELD_VALUE + ":");
            gradesColl = trainingDb.getCollection(COLLECTION_NAME);
            try (MongoCursor<Document> docs = gradesColl.find(eq(STUD_ID_FIELD_NAME, STUD_ID_INSERT_FIELD_VALUE))
                    .iterator()) {
                while (docs.hasNext()) {
                    LOGGER.trace(docs.next().toJson());
                }
            }
            for (Document student : gradesColl.find(eq(STUD_ID_FIELD_NAME, STUD_ID_INSERT_FIELD_VALUE))
                    .into(new ArrayList<>())) {
                LOGGER.trace(student.toJson());
            }
            LOGGER.trace("\n");

            List<Document> grades = new ArrayList<>();
            for (double classId = 1d; classId <= 10d; classId++) {
                grades.add(generateNewGrade(STUD_ID_INSERT_FIELD_VALUE + 1, classId));
            }

            gradesColl.insertMany(grades, new InsertManyOptions().ordered(false));
            LOGGER.trace("Documents in " + COLLECTION_NAME + " for " + STUD_ID_FIELD_NAME + " = "
                    + (STUD_ID_INSERT_FIELD_VALUE + 1) + ":");
            gradesColl = trainingDb.getCollection(COLLECTION_NAME);
            try (MongoCursor<Document> docs = gradesColl.find(eq(STUD_ID_FIELD_NAME, STUD_ID_INSERT_FIELD_VALUE + 1))
                    .iterator()) {
                while (docs.hasNext()) {
                    LOGGER.trace(docs.next().toJson());
                }
            }
            LOGGER.trace("\n");

            List<Document> docs = gradesColl.find(and(eq("student_id", 10001), lte("class_id", 5)))
                    .projection(fields(excludeId(), include("class_id", "student_id"))).sort(descending("class_id"))
                    .skip(2).limit(2).into(new ArrayList<>());

            LOGGER.trace("Student sorted, skipped, limited and projected: ");
            for (Document student : docs) {
                LOGGER.trace(student.toJson());
            }
            LOGGER.trace("\n");
        }
    }

    private static Document generateNewGrade(double studentId, double classId) {
        Random rand = new Random();
        List<Document> scores = Arrays.asList(new Document("type", "exam").append("score", rand.nextDouble() * 100),
                new Document("type", "quiz").append("score", rand.nextDouble() * 100),
                new Document("type", "homework").append("score", rand.nextDouble() * 100),
                new Document("type", "homework").append("score", rand.nextDouble() * 100));
        return new Document("_id", new ObjectId()).append("student_id", studentId).append("class_id", classId)
                .append("scores", scores);
    }
}
