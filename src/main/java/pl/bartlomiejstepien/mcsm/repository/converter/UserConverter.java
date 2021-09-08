package pl.bartlomiejstepien.mcsm.repository.converter;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.repository.ds.Server;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

@Component
public class UserConverter
{
    public UserDto convertToDto(final User user)
    {
        if (user == null)
            return null;

        final UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getPassword());
        userDto.setServerIds(user.getServersIds());
        return userDto;
    }

    public User convertToUser(final UserDto userDto)
    {
        if (userDto == null)
            return null;

        final User user = new User(userDto.getId(), userDto.getUsername(), userDto.getPassword());
        user.setServersIds(userDto.getServerIds());
        return user;
    }
}
