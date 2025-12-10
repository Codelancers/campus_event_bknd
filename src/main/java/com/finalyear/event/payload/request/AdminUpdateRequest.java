package com.finalyear.event.payload.request;


import lombok.Data;


@Data
public class AdminUpdateRequest {
private String name;
private String email; // allow edit
private Integer adminType;
}