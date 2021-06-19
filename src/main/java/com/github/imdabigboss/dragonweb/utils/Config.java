package com.github.imdabigboss.dragonweb.utils;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.github.imdabigboss.dragonweb.DragonWeb;
import com.google.common.io.Resources;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Config {
    private int DEFAULT_PORT = 8080;
    private String DEFAULT_HOSTNAME = "*";
    private String DEFAULT_DIRECTORY = "";
    private List<String> DEFAULT_FORBIDDEN = new ArrayList<>();

    private JSONObject config = null;

    public void saveDefaultConfig() {
        if (new File("config.json").exists()) {
            return;
        }

        String contents = null;
        try {
            URL url = Resources.getResource("config.json");
            contents = Resources.toString(url, StandardCharsets.UTF_8);
        } catch (IOException e) {
            DragonWeb.getLogger().logException(e);
        }

        if (contents == null) {
            DragonWeb.getLogger().error("Your jar file is corrupted, try re-building it or contact a developer.");
            return;
        }

        try (FileWriter file = new FileWriter("config.json")) {
            file.write(contents);
            file.flush();

        } catch (IOException e) {
            DragonWeb.getLogger().error("DragonWeb was unable to save the config.");
            DragonWeb.getLogger().logException(e);
        }
    }

    public void loadConfig() {
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        try (FileReader reader = new FileReader("config.json")) {
            obj = jsonParser.parse(reader);
        } catch (FileNotFoundException e) {
            DragonWeb.getLogger().error("This is not supposed to happen. Please contact a developer.");
            DragonWeb.getLogger().logException(e);
        } catch (IOException e) {
            DragonWeb.getLogger().logException(e);
        } catch (ParseException e) {
            DragonWeb.getLogger().error("DragonWeb was unable to parse the default configuration.");
            DragonWeb.getLogger().logException(e);
        }

        if (obj == null) {
            return;
        }

        config = (JSONObject) obj;
    }

    public void saveConfig() {
        try (FileWriter file = new FileWriter("config.json")) {
            file.write(config.toJSONString());
            file.flush();

        } catch (IOException e) {
            DragonWeb.getLogger().error("DragonWeb was unable to save the config.");
            DragonWeb.getLogger().logException(e);
        }
    }

    public int getPort() {
        if (config == null) {
            DragonWeb.getLogger().error("Config is null.");
            return DEFAULT_PORT;
        }
        if (!config.containsKey("port")) {
            DragonWeb.getLogger().warning("Your config does not have an entry for the port to use.");
            return DEFAULT_PORT;
        }

        return (int) ((long) config.get("port"));
    }
    public String getHostname() {
        if (config == null) {
            DragonWeb.getLogger().error("Config is null.");
            return DEFAULT_HOSTNAME;
        }
        if (!config.containsKey("hostname")) {
            DragonWeb.getLogger().warning("Your config does not have an entry for the hostname to serve on.");
            return DEFAULT_HOSTNAME;
        }

        return (String) config.get("hostname");
    }
    public String getDirectory() {
        if (config == null) {
            DragonWeb.getLogger().error("Config is null.");
            return DEFAULT_DIRECTORY;
        }
        if (!config.containsKey("directory")) {
            DragonWeb.getLogger().error("Your config does not have an entry for a directory to serve.");
            return DEFAULT_DIRECTORY;
        }

        return (String) config.get("directory");
    }
    public List<String> getForbidden() {
        if (config == null) {
            DragonWeb.getLogger().error("Config is null.");
            return DEFAULT_FORBIDDEN;
        }
        if (!config.containsKey("forbidden")) {
            DragonWeb.getLogger().warning("Your config does not have an entry for forbidden files/directories");
            return DEFAULT_FORBIDDEN;
        }

        JSONArray forbidden = (JSONArray) config.get("forbidden");
        List<String> out = new ArrayList<>();
        forbidden.forEach(f -> out.add((String) f));
        return out;
    }

    public JSONObject getConfig() {
        return config;
    }
}
