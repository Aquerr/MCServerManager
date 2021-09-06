package pl.bartlomiejstepien.mcsm.service;

import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.repository.ds.Server;

import java.util.List;
import java.util.Optional;

public interface ServerService
{
    void addServer(int userId, ServerDto serverDto);

    void addServer(Server server);

    List<ServerDto> getServers();

    void deleteServer(int id);

    ServerDto getServer(int id);

    List<ServerDto> getServersForUser(int userId);

    void importServer(int userId, String serverName, String path, Platform platform);

    Optional<ServerDto> getServerByPath(String path);

    Optional<InstallationStatus> getInstallationStatus(int serverId);
}
