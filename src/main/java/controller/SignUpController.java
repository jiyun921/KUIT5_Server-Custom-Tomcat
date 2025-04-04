package controller;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.Map;

import static enums.URL.INDEX_HTML;
import static enums.UserQueryKey.*;
import static enums.UserQueryKey.EMAIL;

public class SignUpController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        String body = request.getBody();
        Map<String, String> userValues = HttpRequestUtils.parseQueryParameter(body);

        User user = new User(
                userValues.get(USERID.getKey()),
                userValues.get(PASSWORD.getKey()),
                userValues.get(NAME.getKey()),
                userValues.get(EMAIL.getKey())
        );
        MemoryUserRepository.getInstance().addUser(user);

        response.redirect(INDEX_HTML.getPath(), false);

//            // GET 방식 - 회원가입
//            if (url.startsWith(SIGNUP.getPath())) {
//                // ?를 기준으로 구분
//                String[] signupUrl = url.split("\\?");
//                if (signupUrl.length==2) {
//                    String signupString = signupUrl[1];
//                    Map<String, String> userValues = HttpRequestUtils.parseQueryParameter(signupString);
//
//                    User user = new User(
//                        userValues.get(USERID.getKey()),
//                        userValues.get(PASSWORD.getKey()),
//                        userValues.get(NAME.getKey()),
//                        userValues.get(EMAIL.getKey())
//                    );
//                    MemoryUserRepository.getInstance().addUser(user);
//                }
//
//                response302Header(dos,0,INDEX_HTML.getPath());
//                return;
//            }
    }
}
