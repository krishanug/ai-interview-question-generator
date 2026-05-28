package com.krish.ai.interviewgenerator.config;

import com.krish.ai.interviewgenerator.constants.AppConstants;
import com.krish.ai.interviewgenerator.exception.RateLimitExceededException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RedisRateLimitingFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    private final int maxRequestsPerWindow;
    private final int windowSeconds;

    public RedisRateLimitingFilter(
            StringRedisTemplate redisTemplate,
            @Value("${app.rate-limit.max-requests-per-window:20}") int maxRequestsPerWindow,
            @Value("${app.rate-limit.window-seconds:60}") int windowSeconds) {
        this.redisTemplate = redisTemplate;
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowSeconds = windowSeconds;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (isGenerationRequest(request)) {
            String clientIp = resolveClientIp(request);
            String key = AppConstants.RateLimit.REDIS_KEY_PREFIX + clientIp;

            Long currentCount = redisTemplate.opsForValue().increment(key);
            if (currentCount != null && currentCount == 1L) {
                redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
            }

            if (currentCount != null && currentCount > maxRequestsPerWindow) {
                throw new RateLimitExceededException(AppConstants.Messages.RATE_LIMIT_EXCEEDED);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isGenerationRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String generatePath = AppConstants.ApiPath.QUESTIONS_BASE + AppConstants.ApiPath.GENERATE;
        return HttpMethod.POST.matches(request.getMethod()) && generatePath.equals(requestUri);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader(AppConstants.RateLimit.HEADER_X_FORWARDED_FOR);
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
