package org.arsiu.rest.exception.item.invalid;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.arsiu.rest.exception.item.ItemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class FormatExceptionHandlerItem {
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Object> handleInvalidFormatException(final InvalidFormatException e) {
        ItemException itemException = new ItemException(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(itemException, HttpStatus.BAD_REQUEST);
    }

}
