package com.github.imdabigboss.dragonweb.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.github.imdabigboss.dragonweb.DragonWeb;
import jline.console.ConsoleReader;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

public class Logger {
    private ConsoleReader console;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private org.apache.log4j.Logger apacheLog = null;
    private org.apache.log4j.Logger fileLog = null;
    public static File file = null;
    private static Set<Logger> loggers = new HashSet();

    public ConsoleReader getConsole() {
        return this.console;
    }

    public Logger(String path) {
        file = new File(path);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException var10) {
                var10.printStackTrace();
            }
        } else {
            long date = file.lastModified();
            String logName = (new SimpleDateFormat("Y-M-d HH.mm.ss")).format(new Date(date)) + ".log";
            File logsPath = new File(DragonWeb.STARTUP_PATH, "/logs");
            if (!logsPath.exists()) {
                logsPath.mkdirs();
            }

            file.renameTo(new File(logsPath, logName));
            file = new File(path);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException var9) {
                    var9.printStackTrace();
                }
            }
        }

        org.apache.log4j.Logger.getRootLogger().setLevel(Level.OFF);
        this.apacheLog = org.apache.log4j.Logger.getLogger("ApacheLogger");
        this.fileLog = org.apache.log4j.Logger.getLogger("FileLogger");
        PatternLayout layout = new PatternLayout("[%d{HH:mm:ss}] %m%n");
        ConsoleAppender ap1 = new ConsoleAppender(layout);
        this.apacheLog.addAppender(ap1);

        try {
            FileAppender f1 = new FileAppender(layout, path, false);
            this.fileLog.addAppender(f1);
        } catch (IOException var8) {
            var8.printStackTrace();
        }

        this.apacheLog.setLevel(Level.INFO);
        this.fileLog.setLevel(Level.INFO);

        try {
            this.console = new ConsoleReader(System.in, System.out);
            this.console.setExpandEvents(false);
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        loggers.add(this);
    }

    public static Logger getLogger() {
        return (Logger)loggers.iterator().next();
    }

    public void error(String message) {
        message = "[§cERROR§r] " + message + "§r";
        this.fileLog.error(Colors.stripColors(message));
        this.apacheLog.error(Colors.toANSI(message));
    }

    public void log(String message) {
        message = "[§bINFO§r] " + message + "§r";
        this.fileLog.info(Colors.stripColors(message));
        this.apacheLog.info(Colors.toANSI(message));
    }

    public void info(String message) {
        log(message);
    }

    public void warning(String message) {
        message = "[§eWARNING§r] " + message + "§r";
        this.fileLog.warn(Colors.stripColors(message));
        this.apacheLog.warn(Colors.toANSI(message));
    }

    public void debug(String message) {
        message = "[§bDEBUG§r] " + message + "§r";
        this.fileLog.info(Colors.stripColors(message));
        this.apacheLog.info(Colors.toANSI(message));
    }

    public void logException(Exception message) {
        warning(Utils.getExceptionMessage(message));
    }
}