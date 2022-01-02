package com.github.imdabigboss.dragonweb.client;

import com.github.imdabigboss.dragonweb.DragonWeb;
import com.github.imdabigboss.dragonweb.client.responses.HTTPResponse;
import com.github.imdabigboss.dragonweb.server.reqests.RequestHeaders;
import com.github.imdabigboss.dragonweb.client.responses.HTTPCodes;

import java.io.*;
import java.net.*;

public class DragonClient extends Thread {
    private Socket clientSocket;
    private OutputStream output = null;
	private InputStream input = null;
    private BufferedReader in = null;

    private int clientID;
    private boolean listening;

    public DragonClient(Socket clientSocket, int clientID) {
        this.clientSocket = clientSocket;
        this.clientID = clientID;
		this.listening = true;

        DragonWeb.getLogger().debug("Client connected (" + clientSocket.getInetAddress() + ") at ID " + clientID);
		this.setName("DragonClient-" + clientID);

        try {
            input = clientSocket.getInputStream();
            in = new BufferedReader(new InputStreamReader(input));

            output = clientSocket.getOutputStream();

            this.start();
        } catch (IOException e) {
            DragonWeb.getLogger().logException(e);
        }
	}

    public RequestHeaders readRequest() throws IOException {
        RequestHeaders req = new RequestHeaders();

        boolean isFirst = true;
        while (listening) {
            String input = in.readLine();
            if (!listening) {
				req.errorCode = HTTPCodes.CODE_500;
                break;
            }

            if (input == null) {
                continue;
            } else if (input.equals("")) {
                if (isFirst) {
                    req.errorCode = HTTPCodes.CODE_400;
                }
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

    public void cleanup() {
        try {
			if (clientSocket != null) {
                clientSocket.close();
            }
			if (input != null) {
				input.close();
			}
            if (in != null) {
                in.close();
            }
            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            DragonWeb.getLogger().logException(e);
        }
    }

    public void closeConnection() {
        cleanup();
        listening = false;
        DragonWeb.getLogger().debug("Connection closed at ID " + clientID + " by Server");
    }

    public OutputStream getOutput() {
        return output;
    }

    public int getClientID() {
        return clientID;
    }

    public void run() {
        if (output == null || in == null || clientSocket == null) {
            cleanup();
            return;
        }

        try {
            while (listening) {
                RequestHeaders req = readRequest();

                long time = System.currentTimeMillis();
                HTTPResponse response = DragonWeb.getServer().processRequest(this, req);
                time = System.currentTimeMillis() - time;
                DragonWeb.getLogger().debug("Request processed in " + time + "ms");

                response.send();
                if (response.getShouldClose()) {
                    closeConnection();
                    return;
                }
            }

            if (!listening) {
                closeConnection();
            }
        } catch (IOException e) {
            DragonWeb.getLogger().debug("Connection closed at ID " + clientID + " by Client");
            cleanup();
        }
    }
}
