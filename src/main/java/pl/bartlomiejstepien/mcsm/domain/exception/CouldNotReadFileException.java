package pl.bartlomiejstepien.mcsm.domain.exception;

public class CouldNotReadFileException extends RuntimeException
{
    public CouldNotReadFileException(String message)
    {
        super(message);
    }
}
