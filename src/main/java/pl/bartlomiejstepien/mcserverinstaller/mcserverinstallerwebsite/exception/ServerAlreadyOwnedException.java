package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.exception;

import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.ServerDto;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.UserDto;

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
