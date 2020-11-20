/*
 * The MIT License
 *
 * Copyright 2017 Leif Lindbäck <leifl@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
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

package se.kth.iv1351.weatherstat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.iv1351.weatherstat.controller.Controller;
import se.kth.iv1351.weatherstat.view.BlockingInterpreter;

/**
 * Starts the weather stats application. Reads the URL of the MongoDB database server hosting the weather database from the environment variable WEATHERDB_SERVER.
 */
public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    /**
     * The environment variable WEATHERDB_SERVER must hold a url pointing to the MongoDB
     * database server hosting the weather database.
     * 
     * @param args There are no command line arguments.
     */
    public static void main(String[] args) {
        try {
        new BlockingInterpreter(new Controller()).handleCmds();
        } catch (Exception exc) {
            LOGGER.error("Could not start application", exc);
        }
    }
}
