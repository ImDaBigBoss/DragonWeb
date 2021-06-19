package com.github.imdabigboss.dragonweb.client.responses;

import com.github.imdabigboss.dragonweb.DragonWeb;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HTTPResponse {
    private static String httpHeader(String httpVersion, HTTPCodes code) {
        return httpVersion + " " + code.getCode() + " " + code.toString();
    }

    public static List<String> httpResponse(String httpVersion, HTTPCodes code, Map<String, String> headers, String body) {
        List<String> out = new ArrayList<>();

        out.add(httpHeader(httpVersion, code));
        for (String header : headers.keySet()) {
            out.add(header + ": " + headers.get(header));
        }
        out.add("Content-length: " + body.getBytes().length);
        out.add("");
        out.add(body + "\r\n");

        return out;
    }

    public static List<String> httpResponse(String httpVersion, HTTPCodes code, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Server", "DragonWeb HTTP server");
        headers.put("Date", new Date().toString());
        headers.put("Content-type", "text/html");

        return httpResponse(httpVersion, code, headers, body);
    }

    public static List<String> httpResponse(String httpVersion, HTTPCodes code, String body, String mime) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Server", "DragonWeb HTTP server");
        headers.put("Date", new Date().toString());
        headers.put("Content-type", mime);

        return httpResponse(httpVersion, code, headers, body);
    }

    public static List<String> httpResponse(String httpVersion, HTTPCodes code) {
        String body;
        try {
            URL url = Resources.getResource("error_page.html");
            body = Resources.toString(url, StandardCharsets.UTF_8).replace("%ERROR_TEXT%", code.getCode() + " " + code.toString());
        } catch (IOException e) {
            DragonWeb.getLogger().error("DragonWeb was unable to find the error page code. This should not happen. Please contact a developer.");
            body = code.toString();
        }

        return httpResponse(httpVersion, code, body);
    }
}
