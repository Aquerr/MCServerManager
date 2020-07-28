package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto;

import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.Server;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;

import javax.persistence.*;

@Entity
@Table(name = "server")
public class ServerDto
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "path")
    private String path;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDto user;

    public ServerDto()
    {

    }

    public ServerDto(int id, String path, UserDto user)
    {
        this.id = id;
        this.path = path;
        this.user = user;
    }

    public static ServerDto fromServer(Server server)
    {
        final UserDto userDto = UserDto.fromUser(server.getUser());
        final ServerDto serverDto = new ServerDto(server.getId(), server.getPath(), userDto);
        userDto.addServer(serverDto);
        return serverDto;
    }

    public int getId()
    {
        return id;
    }

    public String getPath()
    {
        return path;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public UserDto getUser()
    {
        return this.user;
    }

    public void setUser(UserDto user)
    {
        this.user = user;
    }

    public Server toServer()
    {
        return Server.fromDto(this);
    }
}
