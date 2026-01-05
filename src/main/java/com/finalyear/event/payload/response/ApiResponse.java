package com.finalyear.event.payload.response;

import lombok.Data;

@Data
public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;

    // SUCCESS RESPONSE
    public ApiResponse(String message, Object data) {
        this.success = true;
        this.message = message;
        this.data = data;
    }

    // FAILURE RESPONSE
    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
