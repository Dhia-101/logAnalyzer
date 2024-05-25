package com.test.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

enum LogLevel {

    CRITICAL, INFO, WARN, ERROR, DEBUG, TRACE;

    public static LogLevel fromNumeric(int index) {
        switch (index) {
            default:
                return null;
            case 0:
                return LogLevel.INFO;
            case 1:
                return LogLevel.WARN;
            case 2:
                return LogLevel.ERROR;
            case 3:
                return LogLevel.DEBUG;
            case 4:
                return LogLevel.TRACE;
            case 5:
                return LogLevel.CRITICAL;
        }
    }
}

public class LogRecord {

    String[] sources = {"UserManagementModule", "PaymentProcessingModule", "InventoryManagementModule",
        "SecurityModule", "CommunicationModule", "AnalyticsModule", "ErrorHandlingModule", "DatabaseModule"};

    String[] criticalMessages = {
        "Database connection failed: Unable to establish connection to the database server.",
        "Security alert: Unauthorized access attempt detected from IP address 192.168.1.1.",
        "Application error: Out of memory exception occurred while processing user request."
    };

    String[] errorMessages = {
            "Null pointer exception occurred while processing user request.",
            "Failed to parse JSON in request body.",
            "Timeout occurred while trying to connect to external service.",
            "Unexpected token < in JSON at position 0.",
            "Failed to load resource: the server responded with a status of 404 (Not Found)."
    };

    String[] infoMessages = {
            "Server started successfully on port 8080.",
            "User 'admin' logged in successfully.",
            "Database connection established successfully.",
            "File 'config.json' loaded successfully.",
            "Scheduled task 'dailyBackup' executed successfully."
    };

    String[] warnMessages = {
            "Disk usage exceeded 90%.",
            "Database connection pool nearing capacity.",
            "API rate limit nearing threshold.",
            "User 'admin' has attempted multiple unsuccessful login attempts.",
            "Scheduled task 'dailyBackup' took longer than expected to execute."
    };

    String[] debugMessages = {
            "User 'admin' successfully authenticated but session not yet created.",
            "Database connection pool initialized with 10 connections.",
            "GET request received at '/api/v1/users' endpoint.",
            "'dailyBackup' task started at '2022-01-01T00:00:00Z'.",
            "JSON payload '{ \"key\": \"value\" }' received in POST request to '/api/v1/data' endpoint."
    };

    String[] traceMessages = {
            "Entering method calculateTax in TaxService at timestamp 2022-01-01T00:00:00Z.",
            "SQL query 'SELECT * FROM users' executed successfully.",
            "HTTP request headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer token' }.",
            "Trace: Exiting method processPayment in PaymentService with return value true at timestamp 2022-01-01T00:00:01Z.",
            "Session '1234' data: { 'user': 'admin', 'role': 'admin', 'lastAccess': '2022-01-01T00:00:00Z' }."
    };

    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            int year = node.get("year").asInt();
            int month = node.get("monthValue").asInt();
            int day = node.get("dayOfMonth").asInt();
            int hour = node.get("hour").asInt();
            int minute = node.get("minute").asInt();
            int second = node.get("second").asInt();
            int nano = node.get("nano").asInt();
            return LocalDateTime.of(year, month, day, hour, minute, second, nano);
        }
    }

    private static final Random random = new Random();
    private int randomValue;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;
    private LogLevel logLevel;
    private String source;
    private String message;

    public LogRecord() {
        randomValue = random.nextInt(5);
        this.timestamp = LocalDateTime.now();
        this.logLevel = LogLevel.fromNumeric(this.randomValue);
        this.source = sources[random.nextInt(sources.length)];

        if (this.logLevel == LogLevel.CRITICAL) {
            this.message = criticalMessages[random.nextInt(criticalMessages.length)];
        } else if (this.logLevel == logLevel.ERROR) {
            this.message = errorMessages[random.nextInt(errorMessages.length)];
        } else if (this.logLevel == logLevel.INFO) {
            this.message = infoMessages[random.nextInt(infoMessages.length)];
        } else if (this.logLevel == logLevel.WARN) {
            this.message = warnMessages[random.nextInt(warnMessages.length)];
        } else if (this.logLevel == logLevel.DEBUG) {
            this.message = debugMessages[random.nextInt(debugMessages.length)];
        } else if (this.logLevel == logLevel.TRACE) {
            this.message = traceMessages[random.nextInt(traceMessages.length)];
        }

    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getLogLevel() {
        return logLevel.name();
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] (%s): %s", timestamp, logLevel, source, message);
    }
}



