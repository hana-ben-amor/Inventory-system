package com.example.userservice.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus statusCode;
    private String errorMsg;
    private Object body;
    public ErrorResponse(HttpStatus statusCode,String errorMsg){
        this(statusCode,errorMsg,errorMsg);
    }
    public int getStatusCodeValue(){
        return statusCode.value();
    }
}
