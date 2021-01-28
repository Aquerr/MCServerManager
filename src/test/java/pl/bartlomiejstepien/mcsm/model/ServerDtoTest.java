package pl.bartlomiejstepien.mcsm.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.mcsm.exception.ServerNotRunningException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ServerDtoTest
{
    @Test
    public void constructedServerShoukdSetItsNameAndPath()
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
        final UserDto userDto = new UserDto(0, "Test", "Password");
        final ServerDto serverDto = new ServerDto(0, "Test Server", "Test Path");

        serverDto.addUser(userDto);

        assertThat(serverDto.getUsers()).hasSize(1);
        assertThat(serverDto.getUsers()).contains(userDto);
    }

    @Test
    public void userIsRemovedFromTheServer()
    {
        final UserDto userDto = new UserDto(0, "Test", "Password");
        final ServerDto serverDto = new ServerDto(0, "Test Server", "Test Path");
        serverDto.addUser(userDto);

        serverDto.removeUser(userDto);
        assertThat(serverDto.getUsers()).hasSize(0);
        assertThat(serverDto.getUsers()).doesNotContain(userDto);
    }

    @Test
    public void postCommandThrowsIllegalStateExceptionWhenServerIsNotRunning()
    {
        final ServerDto serverDto = new ServerDto(0, "Test Server", "Test Path");
        assertThrows(ServerNotRunningException.class, () -> serverDto.postCommand("help"));
    }
}
