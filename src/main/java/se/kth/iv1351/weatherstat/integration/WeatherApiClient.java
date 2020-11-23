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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class is responsible for calling weather apis. Two hardcoded APIs are
 * used, openweathermap,org and weatherbit.io. The API keys shall be stored in
 * the environment variables OPENWEATHERMAP_KEY and WEATHERBIT_KEY.
 */
public class WeatherApiClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(WeatherApiClient.class);
  private List<String> weatherServices = new ArrayList<>();

  /**
   * Creates a new instance and adds hardcoded APIs.
   */
  public WeatherApiClient() {
    weatherServices.add(
        "http://api.openweathermap.org/data/2.5/weather?q=stockholm,se&APPID=" + System.getenv("OPENWEATHERMAP_KEY"));
    weatherServices
        .add("https://api.weatherbit.io/v2.0/current?city=Stockolm&country=SE&key=" + System.getenv("WEATHERBIT_KEY"));
  }

  /**
   * Returns a list containing the responses from all registered APIs. Only
   * responses with HTTP status codes indicating success (starting with 2 or 3)
   * are included, responses with other status codes are silently ignored.
   * 
   * @return A list with responses from all registered APIs. The list will be
   *         empty if there were no registered APIs, or if there were no
   *         successful responses.
   */
  public List<String> loadFromAllApis() {
    List<String> responses = new ArrayList<>();

    OkHttpClient client = new OkHttpClient();
    for (String weatherService : weatherServices) {

      Request request = new Request.Builder().url(weatherService).build();
      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
          responses.add(response.body().string());
        }
      } catch (IOException ioe) {
        LOGGER.error("Could not load observation.", ioe);
      }

    }

    return responses;
  }
}
