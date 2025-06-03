package com.baraka.matching.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class ExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            HttpClientErrorException.MethodNotAllowed.class,
            MethodArgumentTypeMismatchException.class,
    })
    public ErrorResponse handleBadRequests(Exception e){
        log.error("Unhandled Exception: ", e);
        return new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ErrorResponse handleInternalErrors(Exception e){
        log.error("Unhandled Exception: ", e);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler({
            NoResourceFoundException.class,
            OrderNotFoundException.class
    })
    public ErrorResponse handleNotFound(Exception e){
        return new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
