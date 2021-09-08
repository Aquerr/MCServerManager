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
        serverDto.setUsersIds(server.getUsersIds());
        return serverDto;
    }

    public Server convertToServer(final ServerDto serverDto)
    {
        if (serverDto == null)
            return new Server();

        final Server server = new Server(serverDto.getId(), serverDto.getName(), serverDto.getServerDir());
        server.setPlatform(serverDto.getPlatform());
        server.setUsersIds(serverDto.getUsersIds());
        return server;
    }
}
