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

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final HandlerExceptionResolver handlerExceptionResolver;


    // =========================================================
    // LOCATION LIMITLERİ
    // =========================================================

    @Value("${rate-limit.locations.active.capacity}")
    private int locationsActiveCapacity;

    @Value("${rate-limit.locations.active.duration-minutes}")
    private long locationsActiveDuration;


    @Value("${rate-limit.locations.detail.capacity}")
    private int locationsDetailCapacity;

    @Value("${rate-limit.locations.detail.duration-minutes}")
    private long locationsDetailDuration;


    @Value("${rate-limit.locations.nearby.capacity}")
    private int locationsNearbyCapacity;

    @Value("${rate-limit.locations.nearby.duration-minutes}")
    private long locationsNearbyDuration;


    @Value("${rate-limit.locations.search.capacity}")
    private int locationsSearchCapacity;

    @Value("${rate-limit.locations.search.duration-minutes}")
    private long locationsSearchDuration;


    // =========================================================
    // PUBLIC GENEL LIMIT
    // =========================================================

    @Value("${rate-limit.public.capacity}")
    private int publicCapacity;

    @Value("${rate-limit.public.duration-minutes}")
    private long publicDuration;


    // =========================================================
    // AUTH LIMITLERİ
    // =========================================================

    @Value("${rate-limit.auth.login.capacity}")
    private int loginCapacity;

    @Value("${rate-limit.auth.login.duration-minutes}")
    private long loginDuration;


    @Value("${rate-limit.auth.register.capacity}")
    private int registerCapacity;

    @Value("${rate-limit.auth.register.duration-minutes}")
    private long registerDuration;


    @Value("${rate-limit.auth.capacity}")
    private int authCapacity;

    @Value("${rate-limit.auth.duration-minutes}")
    private long authDuration;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RateLimitFilter(
            RateLimitService rateLimitService,
            HandlerExceptionResolver handlerExceptionResolver) {

        this.rateLimitService = rateLimitService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }


    // =========================================================
    // FILTER
    // =========================================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String path = request.getRequestURI();

            RateLimitRule rule = resolveRateLimitRule(path);

            if (rule != null) {

                String ipAddress = extractClientIp(request);

                rateLimitService.consume(
                        ipAddress,
                        rule.endpointKey(),
                        rule.capacity(),
                        rule.durationMinutes()
                );
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


    // =========================================================
    // RATE LIMIT KURALI BELİRLEME
    // =========================================================

    private RateLimitRule resolveRateLimitRule(String path) {

        /*
         * Daha spesifik endpointler önce kontrol edilmeli.
         */

        // -----------------------------------------------------
        // LOCATION - ACTIVE
        // -----------------------------------------------------

        if (path.equals("/api/public/locations/active")) {

            return new RateLimitRule(
                    "locations-active",
                    locationsActiveCapacity,
                    locationsActiveDuration
            );
        }


        // -----------------------------------------------------
        // LOCATION - SEARCH
        // -----------------------------------------------------

        if (path.equals("/api/public/locations/search")) {

            return new RateLimitRule(
                    "locations-search",
                    locationsSearchCapacity,
                    locationsSearchDuration
            );
        }


        // -----------------------------------------------------
        // LOCATION - NEARBY
        // -----------------------------------------------------

        if (path.equals("/api/public/locations/nearby")) {

            return new RateLimitRule(
                    "locations-nearby",
                    locationsNearbyCapacity,
                    locationsNearbyDuration
            );
        }


        // -----------------------------------------------------
        // LOCATION - DETAIL
        // -----------------------------------------------------


        if (path.startsWith("/api/public/locations/")
                && path.split("/").length == 5) {

            return new RateLimitRule(
                    "locations-detail",
                    locationsDetailCapacity,
                    locationsDetailDuration
            );
        }


        // -----------------------------------------------------
        // AUTH - LOGIN
        // -----------------------------------------------------

        if (path.equals("/api/auth/login")) {

            return new RateLimitRule(
                    "auth-login",
                    loginCapacity,
                    loginDuration
            );
        }


        // -----------------------------------------------------
        // AUTH - REGISTER
        // -----------------------------------------------------

        if (path.equals("/api/auth/register")) {

            return new RateLimitRule(
                    "auth-register",
                    registerCapacity,
                    registerDuration
            );
        }


        // -----------------------------------------------------
        // PUBLIC GENEL
        // -----------------------------------------------------

        if (path.startsWith("/api/public")) {

            return new RateLimitRule(
                    "public-general",
                    publicCapacity,
                    publicDuration
            );
        }


        // -----------------------------------------------------
        // AUTH GENEL
        // -----------------------------------------------------

        if (path.startsWith("/api/auth")) {

            return new RateLimitRule(
                    "auth-general",
                    authCapacity,
                    authDuration
            );
        }


        /*
         * Bu endpoint rate limiting kapsamında değil.
         */
        return null;
    }


    // =========================================================
    // CLIENT IP
    // =========================================================

    private String extractClientIp(
            HttpServletRequest request) {

        String forwardedFor =
                request.getHeader("X-Forwarded-For");

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {

            return forwardedFor
                    .split(",")[0]
                    .trim();
        }

        return request.getRemoteAddr();
    }


    // =========================================================
    // RATE LIMIT RULE
    // =========================================================

    private record RateLimitRule(
            String endpointKey,
            int capacity,
            long durationMinutes
    ) {
    }
}