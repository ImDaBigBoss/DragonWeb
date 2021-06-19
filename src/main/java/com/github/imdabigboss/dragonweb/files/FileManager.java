package com.github.imdabigboss.dragonweb.files;

import com.github.imdabigboss.dragonweb.DragonWeb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {
    public static Path stringToPath(String path) {
        File file = new File(path);

        return file.toPath().toAbsolutePath();
    }

    public static String pathToString(Path path) {
        return path.toAbsolutePath().toString();
    }

    public static boolean fileExists(String path) {
        return new File(path).exists();
    }

    public static String getFileMime(String path) {
        if (!fileExists(path)) {
            return "text/html";
        } else {
            try {
                return Files.probeContentType(stringToPath(path));
            } catch (IOException e) {
                return "";
            }
        }
    }

    public static boolean canServeFile(String path) {
        boolean canServe = true;
        for (String file : DragonWeb.getForbidden()) {
            File fobj = new File(file);
            if (fobj.isDirectory()) {
                if (path.startsWith(file)) {
                    canServe = false;
                    break;
                }
            } else {
                if (path.equals(fobj.getAbsolutePath())) {
                    canServe = false;
                    break;
                }
            }
        }

        return canServe;
    }
}
