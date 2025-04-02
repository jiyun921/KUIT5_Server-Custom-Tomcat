package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// Runnable 구현해서 스레드에서 실행 될 수 있게 함
public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        // 클라이언트와 데이터 주고받기 위한 InputStream, OutputStream 열기
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            // html 화면 반환하기
            String requestLine = br.readLine();
            String[] requestTokens = requestLine.split(" ");
            String url = requestTokens[1];


            // POST 방식 - contentLength 값 가져오기
            int requestContentLength = 0;
            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
            }

            // POST 방식 - 회원가입
            if (url.equals("/user/signup")) {
                String body = IOUtils.readData(br, requestContentLength);
                Map<String, String> userValues = HttpRequestUtils.parseQueryParameter(body);

                User user = new User(
                        userValues.get("userId"),
                        userValues.get("password"),
                        userValues.get("name"),
                        userValues.get("email")
                );
                MemoryUserRepository.getInstance().addUser(user);

                response302Header(dos,"/index.html");
                return;
            }

//            // GET 방식 - 회원가입
//            if (url.startsWith("/user/signup")) {
//                // ?를 기준으로 구분
//                String[] signupUrl = url.split("\\?");
//                if (signupUrl.length==2) {
//                    String signupString = signupUrl[1];
//                    Map<String, String> userValues = HttpRequestUtils.parseQueryParameter(signupString);
//
//                    User user = new User(
//                        userValues.get("userId"),
//                        userValues.get("password"),
//                        userValues.get("name"),
//                        userValues.get("email")
//                    );
//                    MemoryUserRepository.getInstance().addUser(user);
//                }
//
//                response302Header(dos,0,"/index.html");
//                return;
//            }

            // POST 방식 - 로그인
            if (url.equals("/user/login")) {
                String body = IOUtils.readData(br, requestContentLength);
                Map<String, String> userValues = HttpRequestUtils.parseQueryParameter(body);

                String loginId = userValues.get("userId");
                String loginPassword = userValues.get("password");

                User user = MemoryUserRepository.getInstance().findUserById(loginId);
                if (user != null && user.getPassword().equals(loginPassword)) {
                    response302HeaderAddCookie(dos,"/index.html");
                    return;
                }
                response302Header(dos,"/user/login_failed.html");
                return;
            }



            // index.html
            if (url.equals("/")) {
                url = "/index.html";
            }

            // .html 반환
            if (url.endsWith(".html")) {
                File file = new File("./webapp" + url);
                if (file.exists()) {
                    byte[] body = Files.readAllBytes(file.toPath());
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                    return;
                }
            }

            // 응답 본문
            byte[] body = "Hello World".getBytes();
            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response302HeaderAddCookie(DataOutputStream dos, String path) {
        try {
            // 302 Found (리다이렉트)
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            //리다이렉트 위치
            dos.writeBytes("Location: "+ path + "\r\n");
            // 쿠키 추가
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            // empty line
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            // 302 Found (리다이렉트)
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            //리다이렉트 위치
            dos.writeBytes("Location: "+ path + "\r\n");
            // empty line
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 응답 헤더 보내기
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            // 정상적인 응답
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            // 보내는 데이터 html, 인코딩 utf-8
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            // 본문의 바이트 크기
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            // empty line
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 응답 본문 보내기
    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            // body 내용 전송
            dos.write(body, 0, body.length);
            // 버퍼에 쌓인 데이터 모두 보냄
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
