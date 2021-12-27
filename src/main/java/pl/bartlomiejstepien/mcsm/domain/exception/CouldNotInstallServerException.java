package pl.bartlomiejstepien.mcsm.domain.exception;

public class CouldNotInstallServerException extends RuntimeException
{
    public CouldNotInstallServerException(String message)
    {
        super(message);
    }
}
