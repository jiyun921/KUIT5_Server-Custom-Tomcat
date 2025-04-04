package webserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestTest {
    private final String testDirectory = "src/test/java/resources/";

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    @DisplayName("httpRequest 메세지 분석")
    void testHttpRequestMessage() throws IOException {
        // given
        String filePath = testDirectory + "HttpRequestMessage.txt";
        BufferedReader reader = bufferedReaderFromFile(filePath);

        // when
        HttpRequest httpRequest = HttpRequest.from(reader);
        Map<String, String> headers = httpRequest.getHeaders();

        // then
        assertEquals("POST", httpRequest.getStartLine().getMethod());
        assertEquals("/user/create", httpRequest.getStartLine().getPath());
        assertEquals("HTTP/1.1", httpRequest.getVersion());
        assertEquals("localhost:8080", headers.get("Host"));
        assertEquals("keep-alive", headers.get("Connection"));
        assertEquals("40", headers.get("Content-Length"));
        assertEquals("userId=jw&password=password&name=jungwoo", httpRequest.getBody());
    }
}
