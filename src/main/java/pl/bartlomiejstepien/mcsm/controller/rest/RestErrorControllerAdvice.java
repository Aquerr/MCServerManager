package pl.bartlomiejstepien.mcsm.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.bartlomiejstepien.mcsm.exception.ServerAlreadyOwnedException;
import pl.bartlomiejstepien.mcsm.exception.ServerNotOwnedException;
import pl.bartlomiejstepien.mcsm.exception.ServerNotRunningException;

@RestControllerAdvice
public class RestErrorControllerAdvice
{
    @ExceptionHandler(ServerNotRunningException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestErrorResponse handleException(final ServerNotRunningException exception)
    {
        return new RestErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), exception.getMessage());
    }

    @ExceptionHandler(ServerAlreadyOwnedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public RestErrorResponse handleException(final ServerAlreadyOwnedException exception)
    {
        return new RestErrorResponse(HttpStatus.CONFLICT.value(), "Server with the given path is already owned by the user!");
    }

    @ExceptionHandler(ServerNotOwnedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public RestErrorResponse handleException(final ServerNotOwnedException exception)
    {
        return new RestErrorResponse(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }
}
