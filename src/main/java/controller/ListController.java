package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import static enums.URL.LIST_HTML;
import static enums.URL.LOGIN_HTML;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isLogin()) {
            response.redirect(LIST_HTML.getPath(),true);
            return;
        }
        response.redirect(LOGIN_HTML.getPath(),false);
    }
}
