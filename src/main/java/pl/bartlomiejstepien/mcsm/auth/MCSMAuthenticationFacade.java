package pl.bartlomiejstepien.mcsm.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MCSMAuthenticationFacade implements AuthenticationFacade
{
    @Override
    public AuthenticatedUser getCurrentUser()
    {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(AuthenticatedUser.class::cast)
                .orElse(null);
    }

    @Override
    public void setCurrentUser(Authentication authentication)
    {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
