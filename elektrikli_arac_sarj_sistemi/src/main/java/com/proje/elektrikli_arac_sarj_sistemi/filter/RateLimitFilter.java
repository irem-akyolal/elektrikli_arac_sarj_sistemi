package com.proje.elektrikli_arac_sarj_sistemi.filter;

import com.proje.elektrikli_arac_sarj_sistemi.exception.RateLimitExceededException;
import com.proje.elektrikli_arac_sarj_sistemi.service.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("#{'${rate-limit.protected-paths}'.split(',')}")
    private List<String> protectedPaths;

    private final RateLimitService rateLimitService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public RateLimitFilter(
            RateLimitService rateLimitService,
            HandlerExceptionResolver handlerExceptionResolver) {

        this.rateLimitService = rateLimitService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String path = request.getRequestURI();

            if (shouldApplyRateLimit(path)) {

                String ipAddress = extractClientIp(request);
                rateLimitService.consume(ipAddress);
            }

            filterChain.doFilter(request, response);

        } catch (RateLimitExceededException ex) {

            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    ex
            );
        }
    }

    private String extractClientIp(HttpServletRequest request) {

        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private boolean shouldApplyRateLimit(String path) {
        return protectedPaths.stream()
                .anyMatch(path::startsWith);
    }
}