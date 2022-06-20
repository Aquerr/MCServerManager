package pl.bartlomiejstepien.mcsm.web.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerAlreadyOwnedException;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerNotOwnedException;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerNotRunningException;

@RestControllerAdvice
public class RestErrorExceptionHandler
{
    @ExceptionHandler(ServerNotRunningException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "Server is not running")
    public RestErrorResponse handleException(final ServerNotRunningException exception)
    {
        return new RestErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Server is not running");
    }

    @ExceptionHandler(ServerAlreadyOwnedException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Server with given path is already owned")
    public RestErrorResponse handleException(final ServerAlreadyOwnedException exception)
    {
        return new RestErrorResponse(HttpStatus.CONFLICT.value(), "Server with the given path is already owned by the user!");
    }

    @ExceptionHandler(ServerNotOwnedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Access denied")
    public RestErrorResponse handleException(final ServerNotOwnedException exception)
    {
        return new RestErrorResponse(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }
}
