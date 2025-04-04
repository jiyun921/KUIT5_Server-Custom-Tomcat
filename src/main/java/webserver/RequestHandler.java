package webserver;

import controller.*;
import db.MemoryUserRepository;
import db.Repository;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enums.HttpHeader.*;
import static enums.URL.*;


// Runnable 구현해서 스레드에서 실행 될 수 있게 함
public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final Repository repository;
    private Controller controller = new ForwardController();

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        // 클라이언트와 데이터 주고받기 위한 InputStream, OutputStream 열기
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));


            HttpRequest httpRequest  = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(out);

            String url = httpRequest.getPath();
            Map<String, String> headers = httpRequest.getHeaders();
            int requestContentLength = Integer.parseInt(headers.get(CONTENT_LENGTH.getName()));

            // POST 방식 - 회원가입
            if (url.equals(SIGNUP.getPath())) {
                controller = new SignUpController();
            }

            // POST 방식 - 로그인
            if (url.equals(LOGIN.getPath())) {
                controller = new LoginController();
            }

            // userList
            if (url.equals(USERLIST.getPath())) {
                controller = new ListController();
            }

            // index.html
            if (url.equals(ROOT.getPath())) {
                controller = new HomeController();
            }

            // .html, .css 반환
            if (url.endsWith(".html") || url.endsWith(".css")) {
                controller = new ForwardController();
            }

            controller.execute(httpRequest,httpResponse);

        } catch (Exception e) {
            log.log(Level.SEVERE,e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

}
