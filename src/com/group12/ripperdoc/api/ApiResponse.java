package com.group12.ripperdoc.api;

public record ApiResponse(int status, String contentType, String body) {

    public static ApiResponse json(int status, String body) {
        return new ApiResponse(status, "application/json; charset=utf-8", body);
    }

    public static ApiResponse error(int status, String message) {
        return json(status, "{\"error\":" + Json.quote(message) + "}");
    }
}
