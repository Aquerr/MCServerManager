package pl.bartlomiejstepien.mcsm.domain.exception;

import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;

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
