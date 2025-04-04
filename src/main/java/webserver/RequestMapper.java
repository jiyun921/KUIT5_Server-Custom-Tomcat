package webserver;

import controller.*;
import controller.Controller.*;

import java.util.HashMap;
import java.util.Map;

import static enums.URL.*;
import static enums.URL.ROOT;

public class RequestMapper {

    private final HttpRequest request;
    private final HttpResponse response;
    private final Map<String, Controller> controllers = new HashMap<>();

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
        initControllers();
    }

    private void initControllers() {
        controllers.put(ROOT.getPath(), new HomeController());
        controllers.put(SIGNUP.getPath(), new SignUpController());
        controllers.put(LOGIN.getPath(), new LoginController());
        controllers.put(USERLIST.getPath(), new ListController());
    }

    public void proceed() throws Exception {
        String url = request.getPath();
        String method = request.getMethod();

        Controller controller;

        // .html, .css 반환
        if ( method.equals("GET")&& (url.endsWith(".html") || url.endsWith(".css"))) {
            controller = new ForwardController();
        } else {
            controller = controllers.getOrDefault(url, new ForwardController());
        }

        controller.execute(request, response);
    }
}
