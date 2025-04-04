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

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        // 클라이언트와 데이터 주고받기 위한 InputStream, OutputStream 열기
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            HttpRequest httpRequest  = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(out);

            RequestMapper requestMapper = new RequestMapper(httpRequest,httpResponse);
            requestMapper.proceed();

        } catch (Exception e) {
            log.log(Level.SEVERE,e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

}
