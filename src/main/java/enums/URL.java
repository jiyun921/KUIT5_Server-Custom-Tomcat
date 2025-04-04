package enums;

public enum URL {
    ROOT("/"),
    INDEX_HTML("/index.html"),
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    LOGIN_FAILED_HTML("/user/login_failed.html"),
    LOGIN_HTML("/user/login.html"),
    USERLIST("/user/userList"),
    LIST_HTML("/user/list.html");

    private final String path;

    URL(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
