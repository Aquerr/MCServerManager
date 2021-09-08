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
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServerServiceImpl implements ServerService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerServiceImpl.class);

    private final Config config;

    private final ServerRepository serverRepository;
    private final ServerInstaller serverInstaller;
    private final ServerConverter serverConverter;
    private final UserRepository userRepository;

    @Autowired
    public ServerServiceImpl(final Config config,
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
    @Override
    public void addServer(final int userId, final ServerDto serverDto)
    {
        serverDto.getUsersIds().add(userId);
        this.serverRepository.save(this.serverConverter.convertToServer(serverDto));
    }

    @Transactional
    @Override
    public void addServer(final Server server)
    {
        this.serverRepository.save(server);
    }

    @Transactional
    @Override
    public List<ServerDto> getServers()
    {
        return this.serverRepository.findAll().stream().map(serverConverter::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteServer(final int id)
    {
        this.serverRepository.delete(id);
    }

    @Transactional
    @Override
    public ServerDto getServer(final int id)
    {
        final Server server = this.serverRepository.find(id);
        return this.serverConverter.convertToDto(server);
    }

    @Transactional
    @Override
    public List<ServerDto> getServersForUser(final int userId)
    {
        return this.serverRepository.findByUserId(userId).stream()
                .map(this.serverConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void importServer(final int userId, final String serverName, final String path, Platform platform)
    {
        LOGGER.info("Importing server {} for platform {}, at path {}, for user id {} ", serverName, platform.getName(), path, userId);
        final User user = this.userRepository.find(userId);
        final Server existingServerForPath = this.serverRepository.findByPath(path);

        if (existingServerForPath != null)
        {
            if (user.getServersIds().contains(existingServerForPath.getId()))
            {
                throw new ServerAlreadyOwnedException(new UserDto(user.getId(), user.getUsername(), user.getPassword()), new ServerDto(existingServerForPath.getId(), existingServerForPath.getName(), existingServerForPath.getPath()));
            }
            else
            {
                user.getServersIds().add(existingServerForPath.getId());
                this.userRepository.save(user);
            }
        }
        else
        {
            final Server server = new Server(0, serverName, path);
            server.setPlatform(platform.getName());
            server.getUsersIds().add(userId);
            this.serverRepository.save(server);
        }
    }

    @Transactional
    @Override
    public Optional<ServerDto> getServerByPath(final String path)
    {
        final Server server = this.serverRepository.findByPath(path);
        return Optional.ofNullable(server).map(this.serverConverter::convertToDto);
    }

    @Override
    public Optional<InstallationStatus> getInstallationStatus(int serverId)
    {
        return this.serverInstaller.getInstallationStatus(serverId);
    }
}
