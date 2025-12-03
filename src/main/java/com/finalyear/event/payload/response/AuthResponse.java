package com.finalyear.event.payload.response;

import com.finalyear.event.entity.Admin;
import com.finalyear.event.entity.User;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private Admin admin;
    private User user;

    public AuthResponse(String token, Admin admin) {
        this.token = token;
        this.admin = admin;
    }

    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
