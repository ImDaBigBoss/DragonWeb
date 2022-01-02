package com.github.imdabigboss.dragonweb.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    public static String getExceptionMessage(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    public static byte[] readFileBytes(String filepath) {
        try {
            Path path = Paths.get(filepath);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            return new byte[] {};
        }
    }

    public static String readFileContents(String filepath) {
        return new String(readFileBytes(filepath));
    }
}
