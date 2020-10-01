package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.exception.ServerNotRunningException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ServerTest
{
    @Test
    public void constructedServerShoukdSetItsNameAndPath()
    {
        final String serverName = "Test Server";
        final String serverPath = "Test Path";
        final Server server = new Server(0, serverName, serverPath);

        assertEquals(serverName, server.getName());
        assertEquals(serverPath, server.getServerDir());
    }

    @Test
    public void userIsAddedToTheServer()
    {
        final User user = new User(0, "Test", "Password");
        final Server server = new Server(0, "Test Server", "Test Path");

        server.addUser(user);

        assertThat(server.getUsers()).hasSize(1);
        assertThat(server.getUsers()).contains(user);
    }

    @Test
    public void userIsRemovedFromTheServer()
    {
        final User user = new User(0, "Test", "Password");
        final Server server = new Server(0, "Test Server", "Test Path");
        server.addUser(user);

        server.removeUser(user);
        assertThat(server.getUsers()).hasSize(0);
        assertThat(server.getUsers()).doesNotContain(user);
    }

    @Test
    public void postCommandThrowsIllegalStateExceptionWhenServerIsNotRunning()
    {
        final Server server = new Server(0, "Test Server", "Test Path");
        assertThrows(ServerNotRunningException.class, () -> server.postCommand("help"));
    }
}