package com.anthony.orders.utilities;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtils
{
    public static <T> ResponseEntity<T> noContent(){
        return withStatus(HttpStatus.NO_CONTENT);
    }

    public static <T> ResponseEntity<T> internalServerError(){
        return withStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T> ResponseEntity<T> badRequest(){
        return withStatus(HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseEntity<T> accepted(){
        return withStatus(HttpStatus.ACCEPTED);
    }

    private static <T> ResponseEntity<T> withStatus(HttpStatus status){
        return new ResponseEntity<T>(status);
    }
}