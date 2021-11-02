package pl.bartlomiejstepien.mcsm.service;

import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.repository.ds.Server;

import java.util.List;
import java.util.Optional;

public interface ServerService
{
    void addServer(int userId, ServerDto serverDto);

    List<ServerDto> getServers();

    void deleteServer(int id);

    ServerDto getServer(int id);

    List<ServerDto> getServersForUser(int userId);

    void importServer(Integer userId, String serverName, String path, Platform platform, Integer javaId);

    Optional<ServerDto> getServerByPath(String path);

    JavaDto getJavaForServer(int serverId);

    boolean addJavaToServer(int serverId, int javaId);
}
