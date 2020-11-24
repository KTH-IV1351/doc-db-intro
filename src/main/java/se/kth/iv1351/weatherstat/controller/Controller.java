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

package se.kth.iv1351.weatherstat.controller;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.kth.iv1351.weatherstat.integration.WeatherApiClient;
import se.kth.iv1351.weatherstat.integration.WeatherDAO;

/**
 * This is the application's only controller, all calls to the model pass here.
 * The controller is also responsible for calling the DAO. Typically, the
 * controller first calls the DAO to retrieve data (if needed), then operates on
 * the data, and finally tells the DAO to store the updated data (if any).
 */
public class Controller {
    private WeatherDAO weatherDb;
    private WeatherApiClient weatherApi;
    private ScheduledThreadPoolExecutor hourlyLoader;

    /**
     * Creates a new instance, and retrieves a connection to the database.
     * 
     * @throws WeatherDBException If unable to connect to the database.
     */
    public Controller() {
        weatherDb = new WeatherDAO();
        weatherApi = new WeatherApiClient();
    }

    /**
     * Loads the current observation from all weather APIs. This method is blocking
     * and will not return until all APIs have responded, and all data is stored in
     * the database. Only responses with HTTP status codes indicating success
     * (starting with 2 or 3) are stored in the db, responses with other status
     * codes are silently ignored.
     */
    public void loadFromAllApis() {
        List<String> loadedData = weatherApi.loadFromAllApis();
        weatherDb.storeObservations(loadedData);
    }

    /**
     * Starts a separate thread that will call {@link #loadFromAllApis()} once per
     * hour, starting after one hour. The hourly loading continues until
     * {@link #stopHourlyLoading()} is called. A call to this method when hourly
     * loading is already running has no effect.
     */
    public void startHourlyLoading() {
        if (hourlyLoader != null) {
            return;
        }

        int noOfIdleThreads = 0;
        int initialDelay = 1;
        int interval = 1;
        hourlyLoader = new ScheduledThreadPoolExecutor(noOfIdleThreads);
        hourlyLoader.scheduleAtFixedRate(() -> loadFromAllApis(),
                                         initialDelay, interval, TimeUnit.HOURS);
    }

    /**
     * Stops the hourly loading. A call to this method when hourly loading is not
     * running has no effect.
     */
    public void stopHourlyLoading() {
        if (hourlyLoader == null) {
            return;
        }
        hourlyLoader.shutdown();
        hourlyLoader = null;
    }

    /**
     * @return The average of all stored temperature observations.
     */
    public double getAverageTemp() {
        List<Double> tempReadings = weatherDb.findAllTempReadings();
        double sumOfTemps = tempReadings.stream().mapToDouble(Double::doubleValue).sum();
        return sumOfTemps / tempReadings.size();
    }
}
