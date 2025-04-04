package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static enums.HttpHeader.*;
import static enums.HttpStatusCode.*;


public class HttpResponse {
    private final DataOutputStream dos;
    private final Map<String, String> headers = new HashMap<>();
    private final String version = "HTTP/1.1";

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String path) throws IOException{
        File file = new File("./webapp" + path);
        byte[] body = Files.readAllBytes(file.toPath());

        headers.put(CONTENT_TYPE.getName(), getContentType(path));
        headers.put(CONTENT_LENGTH.getName(), String.valueOf(body.length));

        writeStartLine(OK.getCode(),OK.getMessage());
        writeHeaders();
        writeBody(body);
    }

    public void redirect(String location, boolean isLogin) throws IOException {
        writeStartLine(Found.getCode(), Found.getMessage());
        headers.put("Location", location);
        if (isLogin) {
            headers.put(SET_COOKIE.getName(),"logined=true; Path=/");
        }
        writeHeaders();
    }


    private String getContentType(String path) {
        if (path.endsWith(".css")) return "text/css;charset=utf-8";
        return "text/html;charset=utf-8";
    }

    private void writeBody(byte[] body) throws IOException{
        dos.write(body,0, body.length);
        dos.flush();
    }

    private void writeHeaders() throws IOException {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        dos.writeBytes("\r\n");
    }

    private void writeStartLine(int statusCode, String statusMessage) throws IOException {
        dos.writeBytes(version + " " + statusCode + " " + statusMessage + "\r\n");
    }
}
