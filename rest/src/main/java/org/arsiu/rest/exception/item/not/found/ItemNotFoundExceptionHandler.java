package org.arsiu.rest.exception.item.not.found;

import org.arsiu.rest.exception.item.ItemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ItemNotFoundExceptionHandler {
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Object> handleTechniqueNotFoundException(final ItemNotFoundException e) {
        ItemException itemException = new ItemException(
                e.getMessage(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(itemException, HttpStatus.NOT_FOUND);
    }
}
