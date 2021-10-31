package pl.bartlomiejstepien.mcsm.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ServerDtoTest
{
    @Test
    public void constructedServerShouldSetItsNameAndPath()
    {
        final String serverName = "Test Server";
        final String serverPath = "Test Path";
        final ServerDto serverDto = new ServerDto(0, serverName, serverPath);

        assertEquals(serverName, serverDto.getName());
        assertEquals(serverPath, serverDto.getServerDir());
    }

    @Test
    public void userIsAddedToTheServer()
    {
        final UserDto userDto = new UserDto(0, "Test");
        final ServerDto serverDto = new ServerDto(0, "Test Server", "Test Path");

        serverDto.getUsersIds().add(userDto.getId());

        assertThat(serverDto.getUsersIds()).hasSize(1);
        assertThat(serverDto.getUsersIds()).contains(userDto.getId());
    }

    @Test
    public void userIsRemovedFromTheServer()
    {
        final UserDto userDto = new UserDto(0, "Test");
        final ServerDto serverDto = new ServerDto(0, "Test Server", "Test Path");
        serverDto.getUsersIds().add(userDto.getId());

        serverDto.getUsersIds().remove(userDto.getId());
        assertThat(serverDto.getUsersIds()).hasSize(0);
        assertThat(serverDto.getUsersIds()).doesNotContain(userDto.getId());
    }
}
