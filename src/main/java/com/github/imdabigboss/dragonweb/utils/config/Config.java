package com.github.imdabigboss.dragonweb.utils.config;

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
    public List<HostConfig> getHosts() {
        if (config == null) {
            DragonWeb.getLogger().error("Config is null.");
            return new ArrayList<>();
        }
        if (!config.containsKey("hosts")) {
            DragonWeb.getLogger().warning("Your config does not have an entry for forbidden files/directories");
            return new ArrayList<>();
        }

        JSONArray hosts = (JSONArray) config.get("hosts");
        List<HostConfig> out = new ArrayList<>();
        hosts.forEach(c -> {
            JSONObject obj = (JSONObject) c;

            if (obj.containsKey("hostname") && obj.containsKey("directory") && obj.containsKey("forbidden")) {
                String hostname = (String) obj.get("hostname");
                String directory = (String) obj.get("directory");
                JSONArray forbiddenArr = (JSONArray) obj.get("forbidden");
                List<String> forbidden = new ArrayList<>();
                forbiddenArr.forEach(f -> forbidden.add((String) f));
                out.add(new HostConfig(hostname, directory, forbidden));
            }
        });
        return out;
    }

    public JSONObject getConfig() {
        return config;
    }
}
