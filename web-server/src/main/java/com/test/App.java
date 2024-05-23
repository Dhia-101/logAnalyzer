package com.test;

import com.test.db.MySqlConnection;
import com.test.http.service.WebServer;

import org.apache.log4j.Logger;

public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class);

    public static void main(String[] args) {
        final WebServer server = new WebServer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Web server being stopped ...");

            // shut down connection pool
            MySqlConnection.close();

            server.stop();
        }));

        LOGGER.info("Web server starting ...");

        server.start();
    }
}