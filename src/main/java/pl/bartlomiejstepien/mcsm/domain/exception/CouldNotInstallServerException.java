package pl.bartlomiejstepien.mcsm.domain.exception;

public class CouldNotInstallServerException extends RuntimeException
{
    public CouldNotInstallServerException(String message, Throwable throwable)
    {
        super(message, throwable);
    }

    public CouldNotInstallServerException(String message)
    {
        super(message);
    }

    public CouldNotInstallServerException(Exception exception)
    {
        super(exception);
    }
}
