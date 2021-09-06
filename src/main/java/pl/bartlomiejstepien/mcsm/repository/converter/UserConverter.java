package pl.bartlomiejstepien.mcsm.repository.converter;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.repository.ds.McsmPrincipal;

@Component
public class UserConverter
{
    public UserDto convertToDto(final McsmPrincipal mcsmPrincipal)
    {
        return new UserDto(mcsmPrincipal.getId(), mcsmPrincipal.getUsername(), mcsmPrincipal.getPassword());
    }

    public McsmPrincipal convertToDs(UserDto userDto)
    {
        return new McsmPrincipal(userDto.getId(), userDto.getUsername(), userDto.getPassword());
    }
}
