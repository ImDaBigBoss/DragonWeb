package com.github.imdabigboss.dragonweb;

import com.github.imdabigboss.dragonweb.client.DragonClient;
import com.github.imdabigboss.dragonweb.utils.Config;
import com.github.imdabigboss.dragonweb.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import java.util.List;

public class DragonWeb {
    public static final long START_TIME = System.currentTimeMillis();
    public static final String STARTUP_PATH = System.getProperty("user.dir");

    private static boolean listening = true;

    private static List<String> forbidden;
    private static int port;
    private static String hostname;
    private static String directory;

    private static Logger logger;
    private static Config config;

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack" , "true"); //Force IPv4

        if (!(new File(STARTUP_PATH + "/logs/")).exists()) {
            (new File(STARTUP_PATH + "/logs/")).mkdirs();
        }
        logger = new Logger(STARTUP_PATH + "/logs/server.log");
        logger.info("DragonWeb server starting...");

        logger.info("Loading config...");
        config = new Config();
        config.saveDefaultConfig();
        config.loadConfig();

        port = config.getPort();
        hostname = config.getHostname();
        directory = config.getDirectory();
        forbidden = config.getForbidden();

        if (directory == null || directory.equals("")) {
            DragonWeb.getLogger().error("Your chosen directory is not set!");
            return;
        }
        if (!new File(directory).exists()) {
            DragonWeb.getLogger().error("Your chosen directory is does not exist!");
            return;
        }

        directory = new File(directory).getAbsolutePath();

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("Serving: " + directory + " on port: " + port + " and hostname: " + hostname);

            while (listening) {
                new DragonClient(serverSocket.accept());
            }
        } catch (IOException e) {
            DragonWeb.getLogger().logException(e);
        }
    }

    public static int getPort() {
        return port;
    }
    public static String getHostname() {
        return hostname;
    }
    public static String getDirectory() {
        return directory;
    }
    public static List<String> getForbidden() {
        return forbidden;
    }

    public static Logger getLogger() {
        return logger;
    }
    public static Config getConfig() {
        return config;
    }
}
