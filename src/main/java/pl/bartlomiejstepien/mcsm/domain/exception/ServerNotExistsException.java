package pl.bartlomiejstepien.mcsm.domain.exception;

public class ServerNotExistsException extends RuntimeException
{
    public ServerNotExistsException(String message)
    {
        super(message);
    }
}
