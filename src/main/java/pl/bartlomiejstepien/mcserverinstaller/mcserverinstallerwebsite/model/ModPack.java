package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model;

public class ModPack
{
    private final int id;
    private final String name;
    private final String summary;

    private final String thumbnailUrl;

    public ModPack(int id, String name, String summary, String thumbnailUrl)
    {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getSummary()
    {
        return summary;
    }

    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }
}
