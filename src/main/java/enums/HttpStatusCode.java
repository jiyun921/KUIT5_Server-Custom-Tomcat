package enums;

public enum HttpStatusCode {
    OK(200, "OK"),
    Found(302, "Found");

    private final int code;
    private final String message;
    private final String statusline;

    HttpStatusCode(int code, String message) {
        this.code = code;
        this.message = message;
        this.statusline = "HTTP/1.1 " + code + " " + message + " \r\n";
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getStatusline() {
        return statusline;
    }
}
