package com.github.imdabigboss.dragonweb;

import com.github.imdabigboss.dragonweb.server.DragonServer;
import com.github.imdabigboss.dragonweb.utils.config.Config;
import com.github.imdabigboss.dragonweb.utils.Logger;

import javax.swing.*;

import java.io.IOException;

public class DragonWeb {
    private static String STARTUP_PATH = null;

    private static DragonServer server;
    private static Logger logger;
    private static Config config;

    private static boolean mainThreadRunning = true;

    public static void main(String[] args) {
        if (System.console() == null) {
            int selectedOption = JOptionPane.showConfirmDialog(null, "DragonWeb has no console. Do you want to run this anyway? You will not be able to stop the process as it will have no window!", "DragonWeb: Error", JOptionPane.YES_NO_OPTION);
            if (selectedOption != JOptionPane.YES_OPTION) {
                return;
            }
        }

        System.setProperty("java.net.preferIPv4Stack" , "true"); //Force IPv4

        STARTUP_PATH = System.getProperty("user.dir");

        logger = new Logger(STARTUP_PATH + "/logs/");
        logger.info("DragonWeb server starting...");

        logger.info("Loading config...");
        config = new Config();
        config.saveDefaultConfig();
        config.loadConfig();
        logger.setShowDebug(config.getShowDebug());

        server = new DragonServer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdown();
        }));

        try {
            server.listen();
            if (!server.isListening()) {
                logger.info("DragonWeb server stopped.");
            } else {
                logger.warning("DragonWeb server returned. Not good.");
            }
            mainThreadRunning = false;
        } catch (IOException e) {
            DragonWeb.getLogger().logException(e);
        }
    }

    public static void shutdown() {
        DragonWeb.getLogger().info("DragonWeb server stopping...");
        server.shutdown();

        while (mainThreadRunning) {
            
        }

        logger.info("All done. Shutting down...");
    }

    public static Logger getLogger() {
        return logger;
    }
    public static Config getConfig() {
        return config;
    }
    public static DragonServer getServer() {
        return server;
    }
    public static String getStartupPath() {
        return STARTUP_PATH;
    }
}
