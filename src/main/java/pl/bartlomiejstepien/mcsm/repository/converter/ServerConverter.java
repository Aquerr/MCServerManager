package pl.bartlomiejstepien.mcsm.repository.converter;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerOwnerDto;
import pl.bartlomiejstepien.mcsm.repository.ds.McsmPrincipal;
import pl.bartlomiejstepien.mcsm.repository.ds.Server;

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
        for (final McsmPrincipal mcsmPrincipal : server.getUsers())
        {
            final ServerOwnerDto serverOwnerDto = new ServerOwnerDto(mcsmPrincipal.getId(), mcsmPrincipal.getUsername(), server.getId());
            serverDto.addUser(serverOwnerDto);
        }
        return serverDto;
    }

    public Server convertToServer(final ServerDto serverDto)
    {
        if (serverDto == null)
            return new Server();

        final Server server = new Server(serverDto.getId(), serverDto.getName(), serverDto.getServerDir());
        server.setPlatform(serverDto.getPlatform());
        for (final ServerOwnerDto userDto : serverDto.getUsers())
        {
            final McsmPrincipal mcsmPrincipal = new McsmPrincipal(userDto.getId(), userDto.getUsername(), null);
            mcsmPrincipal.addServer(server);
            server.addMcsmPrincipal(mcsmPrincipal);
        }
        return server;
    }
}
