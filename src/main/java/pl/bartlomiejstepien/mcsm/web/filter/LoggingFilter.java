package pl.bartlomiejstepien.mcsm.web.filter;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.auth.AuthenticationFacade;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
@Order(10)
public class LoggingFilter extends AbstractRequestLoggingFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    @Autowired
    public LoggingFilter()
    {
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
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message)
    {
        MDC.remove("username");
    }

    private boolean isStaticResourceRequest(HttpServletRequest httpServletRequest)
    {
        String requestURI = httpServletRequest.getRequestURI();
        return requestURI.startsWith("/css") || requestURI.startsWith("/js") || requestURI.startsWith("/icons");
    }
}
