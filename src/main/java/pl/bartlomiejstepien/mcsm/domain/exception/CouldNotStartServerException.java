package pl.bartlomiejstepien.mcsm.domain.exception;

public class CouldNotStartServerException extends RuntimeException
{

    public CouldNotStartServerException(String message)
    {
        super(message);
    }
}
