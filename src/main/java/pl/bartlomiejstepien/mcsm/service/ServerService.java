package pl.bartlomiejstepien.mcsm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerAlreadyOwnedException;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.domain.server.ServerInstaller;
import pl.bartlomiejstepien.mcsm.repository.ServerRepository;
import pl.bartlomiejstepien.mcsm.repository.UserRepository;
import pl.bartlomiejstepien.mcsm.repository.converter.ServerConverter;
import pl.bartlomiejstepien.mcsm.repository.ds.Server;
import pl.bartlomiejstepien.mcsm.repository.ds.McsmPrincipal;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServerService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerService.class);

    private final Config config;

    private final ServerRepository serverRepository;
    private final ServerInstaller serverInstaller;
    private final ServerConverter serverConverter;
    private final UserRepository userRepository;

    @Autowired
    public ServerService(final Config config,
                         final UserRepository userRepository,
                         final ServerRepository serverRepository,
                         final ServerInstaller serverInstaller,
                         final ServerConverter serverConverter)
    {
        this.config = config;
        this.userRepository = userRepository;
        this.serverRepository = serverRepository;
        this.serverInstaller = serverInstaller;
        this.serverConverter = serverConverter;
    }

    @Transactional
    public void addServer(final int userId, final ServerDto serverDto)
    {
        final McsmPrincipal mcsmPrincipal = this.userRepository.find(userId);
        mcsmPrincipal.addServer(this.serverConverter.convertToServer(serverDto));
    }

    @Transactional
    public void addServer(final Server server)
    {
        this.serverRepository.save(server);
    }

    @Transactional
    public List<ServerDto> getServers()
    {
        return this.serverRepository.findAll().stream().map(serverConverter::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteServer(final int id)
    {
        this.serverRepository.delete(id);
    }

    @Transactional
    public ServerDto getServer(final int id)
    {
        final Server server = this.serverRepository.find(id);
        return this.serverConverter.convertToDto(server);
    }

    @Transactional
    public List<ServerDto> getServersForUser(final int userId)
    {
        return this.serverRepository.findByUserId(userId).stream()
                .map(this.serverConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void importServer(final int userId, final String serverName, final String path, Platform platform)
    {
        final McsmPrincipal mcsmPrincipal = this.userRepository.find(userId);
        final Server server = Optional.ofNullable(this.serverRepository.findByPath(path)).orElse(new Server(0, serverName, path));
        if (server.getId() != 0 && mcsmPrincipal.getServers().stream().anyMatch(x -> path.equals(x.getPath())))
            throw new ServerAlreadyOwnedException(new UserDto(mcsmPrincipal.getId(), mcsmPrincipal.getUsername(), mcsmPrincipal.getPassword()), new ServerDto(server.getId(), server.getName(), server.getPath()));
        server.setPlatform(platform.getName());
        mcsmPrincipal.addServer(server);
        this.userRepository.save(mcsmPrincipal);
    }

    @Transactional
    public Optional<ServerDto> getServerByPath(final String path)
    {
        final Server server = this.serverRepository.findByPath(path);
        return Optional.ofNullable(server).map(this.serverConverter::convertToDto);
    }

    public Optional<InstallationStatus> getInstallationStatus(int serverId)
    {
        return this.serverInstaller.getInstallationStatus(serverId);
    }

    public Optional<ServerDto> getServerForUser(int serverId, int userId)
    {
        return Optional.ofNullable(this.serverRepository.findByServerIdAndUserId(serverId, userId))
                .map(this.serverConverter::convertToDto);
    }
}
