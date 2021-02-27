package pl.bartlomiejstepien.mcsm.domain.exception;

public class ServerNotOwnedException extends RuntimeException
{
    public ServerNotOwnedException(String message)
    {
        super(message);
    }
}
