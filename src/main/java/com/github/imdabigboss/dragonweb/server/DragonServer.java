package com.github.imdabigboss.dragonweb.server;

import com.github.imdabigboss.dragonweb.DragonWeb;
import com.github.imdabigboss.dragonweb.client.DragonClient;
import com.github.imdabigboss.dragonweb.client.responses.HTTPCodes;
import com.github.imdabigboss.dragonweb.client.responses.HTTPResponse;
import com.github.imdabigboss.dragonweb.server.files.FileManager;
import com.github.imdabigboss.dragonweb.server.reqests.RequestHeaders;
import com.github.imdabigboss.dragonweb.utils.Utils;
import com.github.imdabigboss.dragonweb.utils.config.HostConfig;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DragonServer {
    private static Map<Integer, DragonClient> clients = new HashMap<>();

    private static boolean listening = true;
    private static ServerSocket serverSocket = null;

    private static List<HostConfig> hosts = null;
    private static int port = 0;

    public DragonServer() {
        port = DragonWeb.getConfig().getPort();
        hosts = DragonWeb.getConfig().getHosts();
    }

    public HTTPResponse processRequest(DragonClient client, RequestHeaders req) throws IOException {
        if (!listening) {
            HTTPResponse response = new HTTPResponse("HTTP/1.0", HTTPCodes.CODE_500, client.getOutput());
            response.setShouldClose(true);
            return response;
        }

        if (req.errorCode != HTTPCodes.CODE_200) {
            DragonWeb.getLogger().info("Got invalid request");
            HTTPResponse response = new HTTPResponse("HTTP/1.0", HTTPCodes.CODE_400, client.getOutput());
            response.setShouldClose(true);
            return response;
        } else {
            for (HostConfig config : hosts) {
                if (config.checkHostname(config.getHostname(), req.getHeader("Host"))) {
                    String absolutePath = config.getDirectory() + req.getPath();
                    DragonWeb.getLogger().info("Got request for " + req.getPath() + " at " + req.getHeader("Host"));

                    if (new File(absolutePath).isDirectory()) {
                        if (FileManager.fileExists(absolutePath + "index.html")) {
                            absolutePath += "index.html";
                        } else if (FileManager.fileExists(absolutePath + "index.htm")) {
                            absolutePath += "index.htm";
                        } else {
                            return new HTTPResponse(req.getHTTPVersion(), HTTPCodes.CODE_404, client.getOutput());
                        }
                    }

                    if (!FileManager.fileExists(absolutePath)) {
                        return new HTTPResponse(req.getHTTPVersion(), HTTPCodes.CODE_404, client.getOutput());
                    }
                    if (!FileManager.canServeFile(absolutePath, config)) {
                        return new HTTPResponse(req.getHTTPVersion(), HTTPCodes.CODE_403, client.getOutput());
                    }

                    String mime = FileManager.getFileMime(absolutePath);
                    byte[] body = Utils.readFileBytes(absolutePath);
                    return new HTTPResponse(req.getHTTPVersion(), HTTPCodes.CODE_200, body, mime, client.getOutput());
                }
            }

            return new HTTPResponse(req.getHTTPVersion(), HTTPCodes.CODE_404, client.getOutput());
        }
    }

    private String getHostList() {
        String out = "";
        for (HostConfig config : hosts) {
            out += config.getHostname() + " (" + config.getDirectory() + "); ";
        }
        return out;
    }

    public void listen() throws IOException {
        if (hosts == null || port == 0) {
            DragonWeb.getLogger().error("Your configuration is wrong.");
            return;
        }

        serverSocket = new ServerSocket(port);
        DragonWeb.getLogger().info("Serving on port: " + port + " with " + getHostList());

        int clientID = 1;
        while (listening) {
			try {
				Socket clientSocket = serverSocket.accept();
				
				if (listening) {
					clients.put(clientID, new DragonClient(clientSocket, clientID));
				}
	
				clientID++;
			} catch (SocketException e) {
				return;
			}
        }
    }

    public void shutdown() {
        listening = false;
        try {
            serverSocket.close();
        } catch (IOException ignored) {}

        List<DragonClient> clientList = new ArrayList<>(clients.values());
        DragonWeb.getLogger().info("Closing " + clientList.size() + " connections...");
        for (DragonClient client : clientList) {
            client.closeConnection();

			DragonWeb.getLogger().debug("Waiting for thread to finish...");
			while (client.isAlive()) {
				
			}

            clients.remove(client.getClientID());
			DragonWeb.getLogger().info("Connection ID " + client.getClientID() + " stopped.");
        }
    }

    public boolean isListening() {
        return listening;
    }
}
