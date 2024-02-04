package com.example.app;

import android.content.Context;
import android.os.Environment;

import java.io.*;

public class Logger {
    private String LOG_FILE_PATH = "";

    // Constructor to initialize Logger with a specific username
    public Logger(String username, Context context) {
        LOG_FILE_PATH = context.getFilesDir() + File.separator + "history-" + username + ".log";
        // Ensure the log file exists or create it if it doesn't
        createLogFile();
    }

    // Method to write a new log entry to history.log
    public void writeLog(String logType, String logMessage) {
        try (FileWriter fileWriter = new FileWriter(LOG_FILE_PATH, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {

            String logEntry = String.format("[%s] %s", logType, logMessage);
            printWriter.println(logEntry);

            System.out.println("Log entry written successfully.");

        } catch (IOException e) {
            System.err.println("Error writing log entry: " + e.getMessage());
        }
    }

    // Method to read the file line by line and return the content as a String
    public String readLogFile() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n\n");
            }

        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
        }

        return content.toString();
    }

    private void createLogFile() {
        try {
            File logFile = new File(LOG_FILE_PATH);
            if (!logFile.exists()) {
                logFile.createNewFile();
                System.out.println("Log file created successfully: " + LOG_FILE_PATH);
            }
        } catch (IOException e) {
            System.err.println("Error creating log file: " + e.getMessage());
        }
    }
}
