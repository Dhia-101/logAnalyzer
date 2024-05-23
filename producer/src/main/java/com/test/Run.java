package com.test;

import com.test.models.LogRecord;
import com.test.producer.Producer;
import com.test.utils.JsonUtils;
import org.apache.log4j.Logger;

public class Run {
    private static final Logger LOGGER = Logger.getLogger(Run.class);

    public static void main(String[] args) {
        final Producer producer = new Producer();

        // catches ctrl+c action
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Generator application stopping ...");

            producer.close();
        }));

        while (true) {
            try {
                LogRecord logRecord = new LogRecord();
                producer.produce(logRecord.getLogLevel(), JsonUtils.serialize(logRecord));

                Thread.sleep(250);
            } catch (Throwable t) {
                LOGGER.error(t.getMessage(), t);
            }
        }
    }

}