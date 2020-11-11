package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.exception;

import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.Server;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;

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

    public ServerAlreadyOwnedException(User user, Server server)
    {
        super("Username: " + user.getUsername() + ", Server path: " + server.getServerDir());
    }
}
