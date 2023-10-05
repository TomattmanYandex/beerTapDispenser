package com.tomattman.beerTapDispenser.web;

import com.tomattman.beerTapDispenser.exception.DispenserNotFountException;
import com.tomattman.beerTapDispenser.exception.DispenserStatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {DispenserNotFountException.class})
    public ResponseEntity<String> handleNotFound(RuntimeException ex, WebRequest request) {;
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(value = {DispenserStatusException.class})
    public ResponseEntity<String> handleDispenserStatusException(RuntimeException ex, WebRequest request) {
        return ResponseEntity.status(409).body(ex.getMessage());
    }

}
