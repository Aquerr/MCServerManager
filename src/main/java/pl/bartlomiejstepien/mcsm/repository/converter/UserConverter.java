package pl.bartlomiejstepien.mcsm.repository.converter;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.model.Role;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.repository.ds.Server;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import java.util.stream.Collectors;

@Component
public class UserConverter
{
    public UserDto convertToDto(final User user)
    {
        if (user == null)
            return null;

        final UserDto userDto = new UserDto(user.getId(), user.getUsername());
        userDto.setServerIds(user.getServers().stream()
                .map(Server::getId)
                .toList());
        userDto.setRole(Role.findRoleById(user.getRoleId()));
        return userDto;
    }

    public User convertToUser(final UserDto userDto)
    {
        if (userDto == null)
            return null;

        final User user = new User(userDto.getId(), userDto.getUsername(), null, userDto.getRole().getId());
        user.getServers().addAll(userDto.getServerIds().stream()
                .map(this::toServer)
                .toList());
        return user;
    }

    private Server toServer(Integer serverId)
    {
        Server server = new Server();
        server.setId(serverId);
        return server;
    }
}
