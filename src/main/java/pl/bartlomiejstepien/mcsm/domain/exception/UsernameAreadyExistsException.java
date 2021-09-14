package pl.bartlomiejstepien.mcsm.domain.exception;

public class UsernameAreadyExistsException extends RuntimeException
{
    public UsernameAreadyExistsException(String message)
    {
        super(message);
    }
}
