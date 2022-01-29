package pl.bartlomiejstepien.mcsm.web.filter;

import org.jboss.logging.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(2)
public class CorrelationIdFilter extends OncePerRequestFilter
{
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_LOG_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        Optional<String> correlationId = getCorrelationIdFromHttpHeader(request);

        if (correlationId.isEmpty())
        {
            String generatedCorrelationId = generateCorrelationId();
            putCorrelationIdInHttpHeader(response, generatedCorrelationId);
            MDC.put(CORRELATION_ID_LOG_KEY, generatedCorrelationId);
        }

        filterChain.doFilter(request, response);
        MDC.remove(CORRELATION_ID_LOG_KEY);
    }

    private Optional<String> getCorrelationIdFromHttpHeader(HttpServletRequest request)
    {
        return Optional.ofNullable(request.getHeader(CORRELATION_ID_HEADER));
    }

    private void putCorrelationIdInHttpHeader(HttpServletResponse response, String correlationId)
    {
        response.addHeader(CORRELATION_ID_HEADER, correlationId);
    }

    private String generateCorrelationId()
    {
        return UUID.randomUUID().toString();
    }
}
