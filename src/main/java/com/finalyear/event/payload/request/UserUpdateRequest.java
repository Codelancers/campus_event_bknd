package com.finalyear.event.payload.request;


import lombok.Data;


@Data
public class UserUpdateRequest {
    private String name;
    private String email;
    private String phone;
    private String department;
    private Integer year;
    private String rollNo;
}