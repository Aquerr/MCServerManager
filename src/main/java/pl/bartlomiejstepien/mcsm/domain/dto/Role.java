package pl.bartlomiejstepien.mcsm.domain.dto;

public enum Role
{
    USER(1, "USER"),
    ADMIN(2, "ADMIN"),
    OWNER(3, "OWNER");

    final int id;
    final String name;

    Role(int id, String name)
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

    public static Role findRoleById(int id)
    {
        for (Role role : values())
        {
            if (role.getId() == id)
                return role;
        }
        return null;
    }

    public boolean hasMorePrivilegesThan(Role role)
    {
        return ordinal() > role.ordinal();
    }
}
