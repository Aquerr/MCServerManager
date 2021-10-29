package pl.bartlomiejstepien.mcsm.domain.dto;

public enum RoleEnum
{
    USER(1, "USER"),
    ADMIN(2, "ADMIN"),
    OWNER(3, "OWNER");

    final int id;
    final String name;

    RoleEnum(int id, String name)
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

    public static RoleEnum findRoleById(int id)
    {
        for (RoleEnum roleEnum : values())
        {
            if (roleEnum.getId() == id)
                return roleEnum;
        }
        return null;
    }

    public boolean hasMorePrivilegesThan(RoleEnum role)
    {
        return ordinal() > role.ordinal();
    }
}
