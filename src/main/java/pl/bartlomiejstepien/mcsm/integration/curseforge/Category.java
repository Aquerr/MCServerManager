package pl.bartlomiejstepien.mcsm.integration.curseforge;

public enum Category
{
    ALL_CATEGORIES(0, "All Categories"),
    MAGIC(4473, "Magic"),
    MULTIPLAYER(4484, "Multiplayer"),
    EXPLORATION(4476, "Exploration"),
    TECH(4472, "Tech"),
    ADVENTURE_RPG(4475, "Adventure and RPG"),
    LIGHT(4481, "Light"),
    QUESTS(4478, "Quests"),
    MAP_BASED(4480, "Map Based"),
    PVP(4483, "Combat / PvP"),
    HARDCORE(4479, "Hardcore"),
    SKYBLOCK(4736, "Skyblock"),
    FTB(4487, "FTB"),
    EXTRA_LARGE(4482, "Extra Large");

    private final int id;
    private final String name;

    Category(final int id, final String name)
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
}
