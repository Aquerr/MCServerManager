package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model;

import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.ServerDto;

import java.util.ArrayList;
import java.util.List;

public class Server
{
    private int id;
    private String name;

    private String path;

    private User user;

    private List<String> players;

    public static Server fromDAO(final ServerDto serverDto)
    {
        //TODO: Load server information from server.properties.
        final String serverPath = serverDto.getPath();

        return new Server(serverDto.getId(), "", serverDto.getPath(), serverDto.getUser());
    }

    public Server()
    {

    }

    public Server(int id, String name, String path, User user)
    {
        this.id = id;
        this.name = name;
        this.path = path;
        this.user = user;
        this.players = new ArrayList<>();
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public List<String> getPlayers()
    {
        return players;
    }

    public String getPath()
    {
        return this.path;
    }

    public User getUser()
    {
        return user;
    }
}
