package com.github.imdabigboss.dragonweb.client;

import com.github.imdabigboss.dragonweb.DragonWeb;
import com.github.imdabigboss.dragonweb.client.reqests.RequestHeaders;
import com.github.imdabigboss.dragonweb.client.responses.HTTPCodes;
import com.github.imdabigboss.dragonweb.client.responses.HTTPResponse;
import com.github.imdabigboss.dragonweb.files.FileManager;
import com.github.imdabigboss.dragonweb.utils.Utils;
import com.github.imdabigboss.dragonweb.utils.config.HostConfig;

import java.io.*;
import java.net.*;
import java.util.List;

public class DragonClient extends Thread {
    private Socket clientSocket;
    private PrintWriter out = null;
    private BufferedReader in = null;

    public DragonClient(Socket clientSocket) {
        this.clientSocket = clientSocket;

        DragonWeb.getLogger().info("Client connected (" + clientSocket.getInetAddress() + ")");

        try {
            InputStream input = clientSocket.getInputStream();
            in = new BufferedReader(new InputStreamReader(input));

            OutputStream output = clientSocket.getOutputStream();
            out = new PrintWriter(output, true);

            this.start();
        } catch (IOException e) {
            DragonWeb.getLogger().logException(e);
        }
    }

    public RequestHeaders readRequest() throws IOException {
        RequestHeaders req = new RequestHeaders();

        boolean isFirst = true;
        while (true) {
            String input = in.readLine();

            if (input == null) {
                continue;
            } else if (input.equals("")) {
                break;
            }

            if (isFirst) {
                if (!req.parseStart(input)) {
                    req.errorCode = HTTPCodes.CODE_400;
                    break;
                }
                isFirst = false;
            } else {
                req.addHeader(input);
            }
        }

        return req;
    }

    public void sendResponse(List<String> response) {
        for (String line : response) {
            out.println(line);
        }
        out.flush();
    }

    public void sendResponse(String response) {
        out.println(response);
        out.flush();
    }

    public void closeConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {

        }
    }

    public void run() {
        if (out == null || in == null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                DragonWeb.getLogger().logException(e);
            }
            return;
        }

        try {
            while (true) {
                RequestHeaders req = readRequest();
                if (req.errorCode != HTTPCodes.CODE_200) {
                    DragonWeb.getLogger().info("Got invalid request");
                    sendResponse(HTTPResponse.httpResponse("HTTP/1.0", HTTPCodes.CODE_400));
                } else {
                    boolean didRespond = false;
                    for (HostConfig config : DragonWeb.getHosts()) {
                        if (config.checkHostname(config.getHostname(), req.getHeader("Host"))) {
                            String absolutePath = config.getDirectory() + req.getPath();
                            DragonWeb.getLogger().info("Got request for " + req.getPath() + " (" + absolutePath + ")");

                            if (new File(absolutePath).isDirectory()) {
                                if (FileManager.fileExists(absolutePath + "index.html")) {
                                    absolutePath += "index.html";
                                } else if (FileManager.fileExists(absolutePath + "index.htm")) {
                                    absolutePath += "index.htm";
                                } else {
                                    sendResponse(HTTPResponse.httpResponse(req.getHTTPVersion(), HTTPCodes.CODE_404));
                                    continue;
                                }
                            }

                            if (!FileManager.fileExists(absolutePath)) {
                                sendResponse(HTTPResponse.httpResponse(req.getHTTPVersion(), HTTPCodes.CODE_404));
                                continue;
                            }
                            if (!FileManager.canServeFile(absolutePath, config)) {
                                sendResponse(HTTPResponse.httpResponse(req.getHTTPVersion(), HTTPCodes.CODE_403));
                                continue;
                            }

                            String mime = FileManager.getFileMime(absolutePath);
                            String body = Utils.readFileContents(absolutePath);
                            sendResponse(HTTPResponse.httpResponse(req.getHTTPVersion(), HTTPCodes.CODE_200, body, mime));

                            didRespond = true;
                            break;
                        }
                    }

                    if (!didRespond) {
                        sendResponse(HTTPResponse.httpResponse(req.getHTTPVersion(), HTTPCodes.CODE_404));
                    }
                }
            }
        } catch (IOException e) {
            DragonWeb.getLogger().info("Client disconnected");
        }
    }
}
