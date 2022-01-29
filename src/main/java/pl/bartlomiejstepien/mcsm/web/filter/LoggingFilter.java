package pl.bartlomiejstepien.mcsm.web.filter;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.auth.AuthenticationFacade;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class LoggingFilter extends AbstractRequestLoggingFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    private final AuthenticationFacade authenticationFacade;

    @Autowired
    public LoggingFilter(final AuthenticationFacade authenticationFacade)
    {
        this.authenticationFacade = authenticationFacade;
        setBeforeMessagePrefix("");
        setAfterMessageSuffix("");
        setIncludePayload(true);
        setIncludeQueryString(true);
        setIncludeHeaders(false);
        setIncludeClientInfo(false);
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message)
    {
        if (!isStaticResourceRequest(request))
        {
            LOGGER.info(message);
        }
        MDC.put("username", getSecurityUsername());
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message)
    {
        MDC.remove("username");
    }

    private String getSecurityUsername()
    {
        return Optional.ofNullable(this.authenticationFacade.getCurrentUser())
            .map(AuthenticatedUser::getUsername)
            .orElse("");
    }

    private boolean isStaticResourceRequest(HttpServletRequest httpServletRequest)
    {
        String requestURI = httpServletRequest.getRequestURI();
        return requestURI.startsWith("/css") || requestURI.startsWith("/js") || requestURI.startsWith("/icons");
    }
}
