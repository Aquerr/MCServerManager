package pl.bartlomiejstepien.mcsm.auth.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.bartlomiejstepien.mcsm.auth.AuthenticationFacade;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);

    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String BEARER = "Bearer ";

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String jwt = parseJwt(request);
        LOGGER.debug("Validating JWT token: {} for request: {}", jwt, request);
        if (jwt != null && jwtUtils.validateJwtToken(jwt))
        {
            String username = jwtUtils.getUsernameFromJwtToken(jwt);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            authenticationFacade.setCurrentUser(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest httpServletRequest)
    {
        String headerAuth = httpServletRequest.getHeader(AUTHORIZATION_HEADER_KEY);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER))
        {
            return headerAuth.substring(7);
        }
        return null;
    }
}
