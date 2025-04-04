package webserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpResponseTest {

    private final String testDirectory = "src/test/java/resources/";
    private final String forwardPath = "/Httpresponse.txt";

    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }

    @Test
    @DisplayName("httpRequest 메세지 분석 - forward")
    void testHttpReqonseForward() throws IOException {
        //given
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory+forwardPath));

        //when
        httpResponse.forward("/index.html");

        // then
        String result = Files.readString(Paths.get(testDirectory + forwardPath));
        assertTrue(result.startsWith("HTTP/1.1 200 OK"));
        assertTrue(result.contains("Content-Type: text/html"));
    }

    @Test
    @DisplayName("httpRequest 메세지 분석 - 로그인 했을때 redirect")
    void testHttpResponseRedirectLogin() throws Exception {
        // given
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory+forwardPath));

        // when
        httpResponse.redirect("/index.html", true);

        // then
        String result = Files.readString(Paths.get(testDirectory + forwardPath));
        assertTrue(result.startsWith("HTTP/1.1 302 Found"));
        assertTrue(result.contains("Location: /index.html"));
        assertTrue(result.contains("Set-Cookie: logined=true"));
    }

    @Test
    @DisplayName("httpRequest 메세지 분석 - 로그인 안했을때 redirect")
    void testHttpResponseRedirectNotLogin() throws Exception {
        // given
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory+forwardPath));

        // when
        httpResponse.redirect("/index.html", false);

        // then
        String result = Files.readString(Paths.get(testDirectory + forwardPath));
        assertTrue(result.startsWith("HTTP/1.1 302 Found"));
        assertTrue(result.contains("Location: /index.html"));
        assertFalse(result.contains("Set-Cookie"));
    }
}
