package com.finalyear.event.payload.request;


import lombok.Data;


@Data
public class RegisterUserRequest {
private String name;
private String email;
private String phone;
private String department;
private Integer year;
private String rollNo;
}
