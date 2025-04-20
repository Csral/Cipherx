package com.godgamer.frontend;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

import javafx.scene.control.Alert;

public class Logger {
    public static class Log
    {
        public String message;
        public LocalTime time;
        public Log(String message) {
            this.message = message;
            this.time = LocalTime.now();
        }
    }

    static {
        File dir = new File(System.getProperty("user.dir"), "logs");
        if (!(dir.exists() && dir.isDirectory()))
            dir.mkdirs(); // Create the directory if it doesn't exist
    }
    private static ArrayList<Log> logs = new ArrayList<>();

    private static void log(String message) {
        logs.add(new Log(message));
    }

    public static void clearLogs() {
        logs.clear();
    }

    public static ArrayList<Log> getLogs() {
        return new ArrayList<>(logs);
    }

    public static void printLogs() {
        for (Log log : logs) {
            System.out.println(log.time + ": " + log.message);
        }
    }

    public static void saveLogs()
    {
        try {
            File file = new File(System.getProperty("user.dir") + "//logs", "Log_" + App.loginTime.toString().substring(0, 8).replace(":", "_") + ".txt");
            try (FileWriter writer = new FileWriter(file)) {
                for (Log log : logs) {
                    writer.write(log.time + "; " + log.message + "\n");
                }
            }
            System.out.println("File saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // to show warnings
    public static void showWarning(String message, String title, String consoleMessage) {
        log("Warning;"+ title + ";" + message);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
        System.out.println("Warning: " + consoleMessage);
    }

    // to show information
    public static void showInfo(String message, String title, String consoleMessage) {
        log("Info;"+ title + ";" + message);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
        System.out.println("Info: " + consoleMessage);
    }

    // to show errors
    public static void showError(String message, String title, String consoleMessage) {
        log("Error;"+ title + ";" + message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
        System.err.println("Error: " + consoleMessage);
    }
 
    // to print messages in the console
    public static void printMessage(String message, String title) {
        log("Message;"+ title + ";" + message);
        System.out.println("Message: " + title + ": " + message);
    }
    public static void printMessage(String message) {
        log("Message;"+ ";" + message);
        System.out.println("Message: " + message);
    }

    // to print debug messages in the console
    public static void printDebug(String consoleMessage, String title) {
        log("Debug;"+ title + ";" + consoleMessage);
        System.out.println("Debug: " + title + ": " + consoleMessage);
    }
}
