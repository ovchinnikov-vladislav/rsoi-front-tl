package rsoi.lab3.microservices.front.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import rsoi.lab3.microservices.front.model.ErrorResponse;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public ErrorResponse requestHttpMessageNotReadableException(Exception exc) {
        List<FieldError> errors = null;
        if (exc instanceof BindException)
            errors = ((BindException) exc).getBindingResult().getFieldErrors();
        else if (exc instanceof MethodArgumentNotValidException)
            errors = ((MethodArgumentNotValidException) exc).getBindingResult().getFieldErrors();
        if (errors != null) {
            String result = errors.stream().map(err -> String.format("%s: [%s]", err.getField(), err.getDefaultMessage()))
                    .collect(Collectors.joining("; "));
            logger.error("Bad Request: {}", result);
            return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), "Bad Request: " + result, new Date());
        }
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), "Bad Request: " + null, new Date());
    }

}
