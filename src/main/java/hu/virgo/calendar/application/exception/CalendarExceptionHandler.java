package hu.virgo.calendar.application.exception;

import hu.virgo.calendar.domain.validation.CalendarValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CalendarExceptionHandler {

    @ExceptionHandler(CalendarValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Problem handleValidationException(CalendarValidationException ex) {
        return new Problem(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
