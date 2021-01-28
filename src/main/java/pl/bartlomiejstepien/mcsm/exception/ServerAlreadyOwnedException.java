package pl.bartlomiejstepien.mcsm.exception;

import pl.bartlomiejstepien.mcsm.model.ServerDto;
import pl.bartlomiejstepien.mcsm.model.UserDto;

public class ServerAlreadyOwnedException extends RuntimeException
{
    public ServerAlreadyOwnedException()
    {

    }

    public ServerAlreadyOwnedException(String message)
    {
        super(message);
    }

    public ServerAlreadyOwnedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ServerAlreadyOwnedException(UserDto userDto, ServerDto serverDto)
    {
        super("Username: " + userDto.getUsername() + ", Server path: " + serverDto.getServerDir());
    }
}
