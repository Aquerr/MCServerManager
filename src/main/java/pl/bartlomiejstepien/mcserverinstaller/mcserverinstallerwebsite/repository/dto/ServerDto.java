package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto;

import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.Server;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "server")
public class ServerDto
{
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true, insertable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "path")
    private String path;

    @ManyToMany(mappedBy = "servers", cascade = CascadeType.ALL)
    private final List<UserDto> users = new ArrayList<>();

    public ServerDto()
    {

    }

    public ServerDto(int id, String path)
    {
        this.id = id;
        this.path = path;
    }

    public static ServerDto fromServer(Server server)
    {
        if (server == null)
            throw new IllegalArgumentException("Server cannot be null");

        final ServerDto serverDto = new ServerDto(server.getId(), server.getServerDir());
        for (final User user : server.getUsers())
        {
            final UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getPassword());
            userDto.addServer(serverDto);
            serverDto.addUser(userDto);
        }

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

    public List<UserDto> getUsers()
    {
        return this.users;
    }

    public void addUser(UserDto userDto)
    {
        this.users.add(userDto);
    }

    public void addUsers(List<UserDto> userDtos)
    {
        this.users.addAll(userDtos);
    }

    public Server toServer()
    {
        return Server.fromDto(this);
    }
}
