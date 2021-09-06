package pl.bartlomiejstepien.mcsm.auth;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacade
{
    AuthenticatedUser getCurrentUser();

    void setCurrentUser(Authentication authentication);
}
