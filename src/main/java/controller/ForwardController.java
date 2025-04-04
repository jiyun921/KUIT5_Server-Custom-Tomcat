package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.File;

public class ForwardController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        String url = request.getPath();
        File file = new File("./webapp" + url);
        if (file.exists()) {
            response.forward(url);
        }
    }
}
