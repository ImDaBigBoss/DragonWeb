package com.github.imdabigboss.dragonweb.utils;

import org.apache.log4j.*;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;

public class Logger {
    private PatternLayout layout = new PatternLayout();
    private ConsoleAppender consoleAppender = new ConsoleAppender();
    private FileAppender fileAppender = new FileAppender();
    private org.apache.log4j.Logger rootLogger = LogManager.getRootLogger();
    private org.apache.log4j.Logger logger = LogManager.getLogger("DragonWeb");

    private String conversionPattern = "[%d{HH:mm:ss}] %m%n";
    private String currentLogFile = "server.log";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d-M-Y HH.mm.ss");

    private boolean showDebug = false;

    public Logger(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(path + "/" + currentLogFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            long date = file.lastModified();
            String logName = simpleDateFormat.format(new Date(date)) + ".log";
            File logsPath = new File(path);
            if (!logsPath.exists()) {
                logsPath.mkdirs();
            }

            file.renameTo(new File(logsPath, logName));
            file = new File(path);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        layout.setConversionPattern(conversionPattern);

        consoleAppender.setLayout(layout);
        consoleAppender.activateOptions();

        fileAppender.setFile(path + "/" + currentLogFile);
        fileAppender.setLayout(layout);
        fileAppender.activateOptions();

        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(consoleAppender);
        rootLogger.addAppender(fileAppender);
    }

    public boolean getShowDebug() {
        return showDebug;
    }

    public void setShowDebug(boolean showDebug) {
        this.showDebug = showDebug;
    }

    public void error(String message) {
        message = "[" +  Colors.RED + "ERROR" +  Colors.RESET + "] " + message + Colors.RESET;
        logger.error(Colors.toANSI(message));
    }

    public void log(String message) {
        message = "[" +  Colors.AQUA + "INFO" +  Colors.RESET + "] " + message + Colors.RESET;
        logger.info(Colors.toANSI(message));
    }

    public void info(String message) {
        log(message);
    }

    public void warning(String message) {
        message = "[" +  Colors.YELLOW + "WARNING" +  Colors.RESET + "] " + message + Colors.RESET;
        logger.warn(Colors.toANSI(message));
    }

    public void debug(String message) {
        if (showDebug) {
            message = "[" + Colors.AQUA + "DEBUG" + Colors.RESET + "] " + message + Colors.RESET;
            logger.info(Colors.toANSI(message));
        }
    }

    public void logException(Exception message) {
        warning(Utils.getExceptionMessage(message));
    }
}