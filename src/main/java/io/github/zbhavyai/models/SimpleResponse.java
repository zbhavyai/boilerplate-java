package io.github.zbhavyai.models;

public class SimpleResponse {

    private final String message;

    private SimpleResponse(String message) {
        this.message = message;
    }

    public static SimpleResponse create(String message) {
        return new SimpleResponse(message);
    }

    public String getMessage() {
        return this.message;
    }
}
