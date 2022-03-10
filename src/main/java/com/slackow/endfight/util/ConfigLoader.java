/**
 * Author: uksspy
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 uksspy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.slackow.endfight.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

/**
 * Gson Config Loader
 *
 * @author uksspy
 */
public class ConfigLoader {

    private static final JsonParser parser = new JsonParser();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Loads a provided config object from a given JSON file.
     * If the file does not exist it also creates the file using the given object defaults
     *
     * @param clazz The object type you wish to load, also dictates the class of the returned object
     * @param file   The file that is to be created/read from
     * @return The object loaded from file
     */
    public static <T> T loadConfig(Class<T> clazz, File file) throws IOException {
        if (file.createNewFile()) { //File does not exist, save to file
            String json = gson.toJson(parser.parse(gson.toJson(clazz)));
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(json);
            }
        } else { //File exists, load from file
            return gson.fromJson(new String(Files.readAllBytes(file.toPath())), clazz);
        }

        return null;
    }

    /**
     * Saves a config object to the specified file in JSON format
     *
     * @param config The object to be saved
     * @param file   The file to which the object is saved
     */
    public static void saveConfig(Object config, File file) throws IOException {
        file.createNewFile();
        String json = gson.toJson(parser.parse(gson.toJson(config)));
        try (PrintWriter out = new PrintWriter(file)) {
            out.println(json);
        }
    }
}
 
