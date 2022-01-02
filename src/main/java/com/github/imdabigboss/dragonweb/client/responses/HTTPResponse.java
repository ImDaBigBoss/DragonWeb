package com.github.imdabigboss.dragonweb.client.responses;

import com.github.imdabigboss.dragonweb.DragonWeb;

import com.google.common.io.Resources;

import java.io.IOException;
import java.io.OutputStream;

import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.*;

public class HTTPResponse {
    private byte[] body;
    private String mime;
    private HTTPCodes code;
    private String httpVersion;
    private OutputStream outputStream;
    private boolean shouldClose = false;

    public HTTPResponse(String httpVersion, HTTPCodes code, byte[] body, String mime, OutputStream output) {
        this.body = body;
        this.mime = mime;
        this.code = code;
        this.httpVersion = httpVersion;
        this.outputStream = output;
    }

    public HTTPResponse(String httpVersion, HTTPCodes code, OutputStream output) {
        String body;
        try {
            URL url = Resources.getResource("error_page.html");
            body = Resources.toString(url, StandardCharsets.UTF_8).replace("%ERROR_TEXT%", code.getCode() + " " + code);
        } catch (IOException e) {
            DragonWeb.getLogger().error("DragonWeb was unable to find the error page code. This should not happen. Please contact a developer.");
            body = code.toString();
        }

        this.body = body.getBytes();
        this.mime = "text/html";
        this.code = code;
        this.httpVersion = httpVersion;
        this.outputStream = output;
    }

    public boolean getShouldClose() {
        return shouldClose;
    }
    public void setShouldClose(boolean shouldClose) {
        this.shouldClose = shouldClose;
    }

    private static void printlnToOutput(OutputStream output, String text) throws IOException {
        text += "\r\n";
        output.write(text.getBytes());
    }

    private static String httpHeader(String httpVersion, HTTPCodes code) {
        return httpVersion + " " + code.getCode() + " " + code;
    }

    public void send() throws IOException {
        printlnToOutput(outputStream, httpHeader(httpVersion, code));
        printlnToOutput(outputStream, "Accept-Ranges: bytes");
        printlnToOutput(outputStream, "Server: DragonWeb HTTP server");
        printlnToOutput(outputStream, "Date: " + new Date());
        printlnToOutput(outputStream, "Content-type: " + mime);
        printlnToOutput(outputStream, "Content-length: " + body.length);
        printlnToOutput(outputStream, "");
        outputStream.write(body);
        outputStream.flush();
    }
}
