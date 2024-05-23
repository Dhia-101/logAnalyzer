package com.test.http.responses;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MetricResponse {

    public static class LogLevelMetric {

        private String logLevel;
        private long logLevelCount;

        public String getLogLevel() {
            return logLevel;
        }

        public void setLogLevel(String logLevel) {
            this.logLevel = logLevel;
        }

        public long getLogLevelCount() {
            return logLevelCount;
        }

        public void setLogLevelCount(long logLevelCount) {
            this.logLevelCount = logLevelCount;
        }
    }

    private List<LogLevelMetric> logLevelMetrics;

    private Timestamp lastTime;

    public List<LogLevelMetric> getLogLevelMetrics() {
        return logLevelMetrics;
    }

    public void setLogLevelMetrics(List<LogLevelMetric> metrics) {
        this.logLevelMetrics = metrics;
    }

    public void addLogLevelMetric(LogLevelMetric logLevelMetric) {
        if (this.logLevelMetrics == null) {
            logLevelMetrics = new ArrayList<>();
        }

        logLevelMetrics.add(logLevelMetric);
    }

    public Timestamp getLastTime() {
        return lastTime;
    }

    public void setLastTime(Timestamp lastTime) {
        this.lastTime = lastTime;
    }
}