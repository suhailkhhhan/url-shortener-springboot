package com.urlshortener.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UrlResponse {

    private String shortCode;
    private String longUrl;
    private int clickCount;
}
