package com.invoice.springboot.controller;

import com.invoice.springboot.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ExceptionControllerAdvice
{
    public ExceptionControllerAdvice() {}

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex)
    {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setMessage(ex.getMessage());
        return new ResponseEntity(error, HttpStatus.OK);
    }
}
