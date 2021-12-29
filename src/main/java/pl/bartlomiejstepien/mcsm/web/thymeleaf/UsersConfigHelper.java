package pl.bartlomiejstepien.mcsm.web.thymeleaf;

import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;

import java.util.Objects;

public final class UsersConfigHelper
{
    private UsersConfigHelper()
    {

    }

    public static boolean canEditUser(AuthenticatedUser user, UserDto targetUser)
    {
        if (Objects.equals(user.getId(), targetUser.getId()))
            return true;
        return user.getRole().hasMorePrivilegesThan(targetUser.getRole());
    }

    public static boolean canDeleteUser(AuthenticatedUser user, UserDto targetUser)
    {
        return user.getRole().hasMorePrivilegesThan(targetUser.getRole());
    }
}
