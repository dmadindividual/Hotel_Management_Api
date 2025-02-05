package topg.bimber_user_service.config;


import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter implements Filter {
    private final RateLimiterService rateLimiterService;



    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String ipAddress = httpRequest.getRemoteAddr(); // Get client IP address
        Bucket bucket = rateLimiterService.getBucket(ipAddress);

        if (bucket.tryConsume(1)) {  // Allow request if token is available
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("Too many requests. Try again later.");
        }
    }
}
