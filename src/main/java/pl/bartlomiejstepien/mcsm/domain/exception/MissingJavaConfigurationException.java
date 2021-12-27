package pl.bartlomiejstepien.mcsm.domain.exception;

public class MissingJavaConfigurationException extends RuntimeException
{
    public MissingJavaConfigurationException(String message)
    {
        super(message);
    }
}
