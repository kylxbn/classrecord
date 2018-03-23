/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord;

import javafx.application.Application;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

class Main {
    private static void changeLoggingLevel(Level l) {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(l);
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(l);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        changeLoggingLevel(Level.OFF);

        for (String arg : args) {
            switch (arg) {
                case "--debug":
                    System.out.println("Debug mode activated.");
                    changeLoggingLevel(Level.INFO);
                    break;
                case "--reset":
                    System.out.println("Resetting program.");
                    try {
                        FileUtils.deleteDirectory(new File("database"));
                    } catch (IOException e) {
                        System.out.println("Unable to reset program.");
                    }
                    break;
                case "--help":
                case "-h":
                    System.out.println("Class Record -- available switches\n");
                    System.out.println("--debug         If you don't know what it is, don't use it.");
                    System.out.println("--reset         Deletes all records and starts the program as if brand new.");
                    System.out.println("--help or -h    Shows this screen.\n");
                    System.out.println("Program will now exit.");
                    return;
                default:
                    System.out.println("Invalid argument '" + arg + ". Exiting program.");
                    return;
            }
        }

        System.setProperty("javafx.preloader", "com.orthocube.classrecord.MainPreloader");
        Application.launch(MainApp.class, args);
    }
}
