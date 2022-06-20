package pl.bartlomiejstepien.mcsm.domain.exception;

public class ServerNotRunningException extends RuntimeException
{
    public ServerNotRunningException()
    {
        super("Server is not running!");
    }

    public ServerNotRunningException(final Throwable throwable)
    {
        super("Server is not running!", throwable);
    }

    public ServerNotRunningException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }
}
