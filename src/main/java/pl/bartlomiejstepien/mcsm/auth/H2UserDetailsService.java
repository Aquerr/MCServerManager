package pl.bartlomiejstepien.mcsm.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.bartlomiejstepien.mcsm.domain.dto.RoleEnum;
import pl.bartlomiejstepien.mcsm.repository.UserRepository;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import javax.servlet.http.HttpServletRequest;

@Component("userDetailsService")
public class H2UserDetailsService implements UserDetailsService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(H2UserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        LOGGER.info("Login attempt for username {} from {}", username, getClientIp(httpServletRequest));

        final User user = this.userRepository.findByUsername(username);

        if (user == null)
            throw new UsernameNotFoundException("Could not find user with username = " + username);

        return new AuthenticatedUser(user.getId(), user.getUsername(), user.getPassword(), getClientIp(httpServletRequest), RoleEnum.findRoleById(user.getRoleId()));
    }

    private static String getClientIp(HttpServletRequest request)
    {
        String remoteAddr = "";

        if (request != null)
        {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr))
            {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}
