package pl.bartlomiejstepien.mcsm.exception;

public class ServerNotOwnedException extends RuntimeException
{
    public ServerNotOwnedException(String message)
    {
        super(message);
    }
}
