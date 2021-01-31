package pl.bartlomiejstepien.mcsm.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.bartlomiejstepien.mcsm.exception.ServerAlreadyOwnedException;
import pl.bartlomiejstepien.mcsm.exception.ServerNotRunningException;

@RestControllerAdvice
public class RestErrorController
{
    @ExceptionHandler(ServerNotRunningException.class)
    public ResponseEntity<?> handleException(final ServerNotRunningException exception)
    {
        final RestErrorResponse restErrorResponse = new RestErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), exception.getMessage());
        return ResponseEntity.badRequest().body(restErrorResponse);
    }

    @ExceptionHandler(ServerAlreadyOwnedException.class)
    public ResponseEntity<?> handleException(final ServerAlreadyOwnedException exception)
    {
        final RestErrorResponse restErrorResponse = new RestErrorResponse(HttpStatus.CONFLICT.value(), "Server with the given path is already owned by the user!");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(restErrorResponse);
    }

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<?> handleException(final RuntimeException exception)
//    {
//        final RestErrorResponse restErrorResponse = new RestErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(restErrorResponse);
//    }
}
