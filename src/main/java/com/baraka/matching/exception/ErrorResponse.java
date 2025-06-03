package com.baraka.matching.exception;

import org.springframework.http.HttpStatus;


public record ErrorResponse(HttpStatus status, String message) {
}
