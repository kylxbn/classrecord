/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util;

import java.io.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Settings {
    private final static Logger LOGGER = Logger.getLogger(Settings.class.getName());

    public static boolean isDark = true;
    public static Locale language = Locale.getDefault();
    public static int languageCode = -1;
    public static ResourceBundle bundle;

    // static initializer
    static {
        LOGGER.log(Level.INFO, "Trying to read settings...");
        try {
            File file = new File("classrecord.config");
            if (file.exists()) {
                LOGGER.log(Level.INFO, "Config file found. Using it.");

                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] pair = line.split("=");
                    switch (pair[0]) {
                        case "dark":
                            isDark = pair[1].equals("true");
                            break;
                        case "language":
                            language = Locale.forLanguageTag(pair[1]);
                            if (pair[1].contains("en")) languageCode = 0;
                            else languageCode = 1;
                    }
                }
                fileReader.close();
            } else {
                LOGGER.log(Level.INFO, "Config file not found. Using defaults.");
                if (Locale.getDefault().toLanguageTag().contains("en")) languageCode = 0;
                else languageCode = 1;
            }

            bundle = ResourceBundle.getBundle("com.orthocube.classrecord.bundles.strings", language);
        } catch (IOException e) {
            Dialogs.exception(e);
        }
    }

    public static void save() {
        try {
            LOGGER.log(Level.INFO, "Saving settings...");
            File file = new File("classrecord.config");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("dark=" + (isDark ? "true\n" : "false\n"));
            fileWriter.write("language=" + language.toLanguageTag() + "\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ex) {
            Dialogs.exception(ex);
        }
    }
}
