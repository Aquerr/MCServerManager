package pl.bartlomiejstepien.mcsm.domain.model;

public class ServerPack
{
    private final int id;
    private final String name;

    public ServerPack(final int id, final String name)
    {
        this.id = id;
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return "ServerPack{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
