package pl.bartlomiejstepien.mcsm.repository.converter;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.repository.ds.Server;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import java.io.File;

@Component
public class ServerConverter
{
    public ServerDto convertToDto(final Server server)
    {
        if (server == null)
            return new ServerDto();

        final ServerDto serverDto = new ServerDto(server.getId(), server.getPath().substring(server.getPath().lastIndexOf(File.separator) + 1), server.getPath());
        serverDto.setPlatform(server.getPlatform());
        for (final User user : server.getUsers())
        {
            final UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getPassword());
            serverDto.addUser(userDto);
            userDto.addServer(serverDto);
        }
        return serverDto;
    }

    public Server convertToServer(final ServerDto serverDto)
    {
        if (serverDto == null)
            return new Server();

        final Server server = new Server(serverDto.getId(), serverDto.getName(), serverDto.getServerDir());
        server.setPlatform(serverDto.getPlatform());
        for (final UserDto userDto : serverDto.getUsers())
        {
            final User user = new User(userDto.getId(), userDto.getUsername(), userDto.getPassword());
            user.addServer(server);
            server.addUser(user);
        }
        return server;
    }
}
