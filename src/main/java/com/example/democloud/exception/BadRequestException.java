package com.example.democloud.exception;

import lombok.Data;

@Data
public class BadRequestException extends RuntimeException{

    private final String message;

    public BadRequestException(String message) {
        super(message);
        this.message = message;
    }
}
