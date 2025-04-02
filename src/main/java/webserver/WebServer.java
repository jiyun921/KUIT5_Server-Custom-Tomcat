package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class WebServer {
    private static final int DEFAULT_PORT = 80; //기본 포트를 80으로 지정
    private static final int DEFAULT_THREAD_NUM = 50; // 한번에 최대 50개 스레드까지 처리 가능
    private static final Logger log = Logger.getLogger(WebServer.class.getName()); // 서버 로그 출력하기 위한 Logger 객체

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        ExecutorService service = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM); // 스레드 풀 만들기 - 여러 클라이언트 요청을 동시에 처리하기 위해 팔수

        // 프로그램 실행 시 포트 지정하면 그 포트 사용
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        // TCP 환영 소켓
        try (ServerSocket welcomeSocket = new ServerSocket(port)){

            // 연결 소켓
            Socket connection;
            // 클라이언트 연결 들어오면 accept()가 새로운 소켓 반환
            while ((connection = welcomeSocket.accept()) != null) {
                // 스레드에 작업 전달
                service.submit(new RequestHandler(connection));
            }
        }

    }
}
