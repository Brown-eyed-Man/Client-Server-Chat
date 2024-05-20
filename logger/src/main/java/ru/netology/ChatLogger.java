package ru.netology;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatLogger {
    private static ChatLogger instance = null;
    private final static String loggerFilePath = "server/src/main/java/ru/netology/File.log";
    private static final File loggerFile = new File(loggerFilePath);

    private ChatLogger() {
    }

    public static ChatLogger getInstance() {
        if (instance == null) {
            instance = new ChatLogger();
        }
        return instance;
    }

    public void log(String message) {
        if (!loggerFile.exists()) {
            try {
                loggerFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при создании файла: " + e);
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(loggerFilePath, true))) {
            String currentTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now());
            writer.write(String.format("[%s] %s", currentTime, message));
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
