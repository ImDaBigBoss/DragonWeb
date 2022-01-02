package com.github.imdabigboss.dragonweb.server.reqests;

import com.github.imdabigboss.dragonweb.client.responses.HTTPCodes;

import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {
    private Map<String, String> headers = new HashMap<>();
    private ReqestType type = ReqestType.GET;
    private String path = "/";
    private String httpVersion = "HTTP/1.0";
    public HTTPCodes errorCode = HTTPCodes.CODE_200;

    public void addHeader(String header) {
        if (!header.contains(":")) {
            return;
        }
        String[] parts = header.split(":");
        if (parts.length < 2) {
            return;
        }

        String name = parts[0];
        String contents;
        if (header.startsWith(name + ": ")) {
            contents = header.replace(name + ": ", "");
        } else if (header.startsWith(name + ":")) {
            contents = header.replace(name + ":", "");
        } else {
            return;
        }

        addHeader(name, contents);
    }

    public void addHeader(String name, String contents) {
        headers.put(name, contents);
    }

    public boolean parseStart(String input) {
        String[] parts = input.split(" ");
        if (parts.length != 3) {
            errorCode = HTTPCodes.CODE_400;
            return false;
        }

        try {
            type = ReqestType.valueOf(parts[0]);
        } catch (IllegalArgumentException e) {
            errorCode = HTTPCodes.CODE_400;
            return false;
        }
        path = parts[1];
        httpVersion = parts[2];

        if (httpVersion.equals("HTTP/1.0") || httpVersion.equals("HTTP/1.1")) {
            return true;
        } else {
            errorCode = HTTPCodes.CODE_400;
            return false;
        }
    }

    public ReqestType getType() {
        return type;
    }
    public String getPath() {
        return path;
    }
    public String getHTTPVersion() {
        return httpVersion;
    }
    public Map<String, String> getAllHeaders() {
        return headers;
    }
    public String getHeader(String header) {
        if (headers.containsKey(header)) {
            return headers.get(header);
        } else {
            return "";
        }
    }
}
