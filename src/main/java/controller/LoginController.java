package controller;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.Map;

import static enums.URL.*;
import static enums.UserQueryKey.PASSWORD;
import static enums.UserQueryKey.USERID;

public class LoginController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        String body = request.getBody();

        Map<String, String> userValues = HttpRequestUtils.parseQueryParameter(body);

        String loginId = userValues.get(USERID.getKey());
        String loginPassword = userValues.get(PASSWORD.getKey());

        User user = MemoryUserRepository.getInstance().findUserById(loginId);
        if (user != null && user.getPassword().equals(loginPassword)) {
            response.redirect(INDEX_HTML.getPath(), true);
            return;
        }
        response.redirect(LOGIN_FAILED_HTML.getPath(), false);
    }
}
