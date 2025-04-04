package webserver;

import enums.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static enums.HttpHeader.CONTENT_LENGTH;
import static enums.HttpHeader.COOKIE;

public class HttpRequest {
    private final HttpStartLine startLine;
    private final Map<String, String> headers = new HashMap<>();
    private final String body;

    public HttpRequest(HttpStartLine startLine, Map<String, String> headers, String body) {
        this.startLine = startLine;
        this.headers.putAll(headers);
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        //start line
        String requestLine = br.readLine();
        String[] requestTokens = requestLine.split(" ");
        HttpStartLine startLine = new HttpStartLine(requestTokens[0], requestTokens[1], requestTokens[2]);

        // headers
        Map<String, String> headers = new HashMap<>();
        int requestContentLength = 0;

        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }

            int colonIndex = line.indexOf(":");
            if (colonIndex != -1) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);

                if (CONTENT_LENGTH.getName().equalsIgnoreCase(key)) {
                    requestContentLength = Integer.parseInt(value);
                }

            }
        }

        // Body
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = 0; i < requestContentLength; i++) {
            bodyBuilder.append((char) br.read());
        }
        String body = bodyBuilder.toString();

        return new HttpRequest(startLine, headers, body);
    }

    public boolean isLogin() {
        String CookieHeader = headers.get(COOKIE.getName());
        if (CookieHeader == null) {
            return false;
        }


        String[] cookies = CookieHeader.split(";");
        for (String cookie : cookies) {
            String[] kv = cookie.trim().split("=");
            if (kv.length == 2) {
                String cookieKey = kv[0].trim();
                String cookieValue = kv[1].trim();
                if (cookieKey.equals("logined") && cookieValue.equals("true")) {
                    return true;
                }
            }
        }
        return false;
    }

    public HttpStartLine getStartLine() {
        return startLine;
    }

    public String getMethod() {
        return startLine.getMethod();
    }
    public String getPath() {
        return startLine.getPath();
    }
    public String getVersion() {
        return startLine.getVersion();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
