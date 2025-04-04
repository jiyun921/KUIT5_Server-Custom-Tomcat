package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import static enums.URL.INDEX_HTML;
import static enums.URL.ROOT;

public class HomeController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        String url = request.getPath();

        if (url.equals(ROOT.getPath())) {
            url = INDEX_HTML.getPath();
        }

        response.forward(url);
    }
}
