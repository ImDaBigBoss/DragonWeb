package com.github.imdabigboss.dragonweb;

import com.github.imdabigboss.dragonweb.client.DragonClient;
import com.github.imdabigboss.dragonweb.utils.config.Config;
import com.github.imdabigboss.dragonweb.utils.Logger;
import com.github.imdabigboss.dragonweb.utils.config.HostConfig;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import java.util.List;

public class DragonWeb {
    public static final long START_TIME = System.currentTimeMillis();
    public static final String STARTUP_PATH = System.getProperty("user.dir");

    private static boolean listening = true;

    private static List<HostConfig> hosts;
    private static int port;

    private static Logger logger;
    private static Config config;

    public static void main(String[] args) {
        if (System.console() == null) {
            int selectedOption = JOptionPane.showConfirmDialog(null, "DragonWeb has no console. Do you want to run this anyway? You will not be able to stop the process as it will have no window!", "DragonWeb: Error", JOptionPane.YES_NO_OPTION);
            if (selectedOption != JOptionPane.YES_OPTION) {
                return;
            }
        }

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
        hosts = config.getHosts();

        if (hosts == null) {
            DragonWeb.getLogger().error("Your configuration is wrong.");
            return;
        }

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("Serving: on port: " + port);

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
    public static List<HostConfig> getHosts() {
        return hosts;
    }

    public static Logger getLogger() {
        return logger;
    }
    public static Config getConfig() {
        return config;
    }
}
