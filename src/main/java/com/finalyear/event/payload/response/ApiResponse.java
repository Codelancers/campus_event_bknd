package com.finalyear.event.payload.response;

import lombok.Data;

@Data
public class ApiResponse {
    private boolean success = true;
    private String message;
    private Object data;

    public ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }
}
