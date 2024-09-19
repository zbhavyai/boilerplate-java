package io.github.zbhavyai.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
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
