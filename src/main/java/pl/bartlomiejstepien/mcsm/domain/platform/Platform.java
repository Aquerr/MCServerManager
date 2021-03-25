package pl.bartlomiejstepien.mcsm.domain.platform;

public enum Platform
{
    BUKKIT("Bukkit", "Server that allows installation of plugins", "/icons/Bukkit@2x.png", false),
    SPIGOT("Spigot", "A high performance customized CraftBukkit Minecraft server that support Bukkit and Spigot plugins", "/icons/Spigot@2x.png", false),
    FORGE("Forge", "Server that allows installation of mods", "/icons/Forge@2x.png", true),
    SPONGE("Sponge", "Server that allows installation of mods and Sponge plugins", "/icons/Sponge@2x.png", false);

    private final String name;
    private final String description;
    private final String imagePath;
    private final boolean available;

    Platform(String name, String description, String imagePath, boolean available)
    {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.available = available;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean isAvailable()
    {
        return available;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    @Override
    public String toString()
    {
        return "Platform{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                '}';
    }
}
