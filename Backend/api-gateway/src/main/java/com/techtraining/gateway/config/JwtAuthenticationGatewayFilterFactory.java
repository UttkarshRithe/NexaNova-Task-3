package com.techtraining.gateway.config;

import com.techtraining.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public JwtAuthenticationGatewayFilterFactory() {
        super(Config.class);
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            // ✅ ALLOW CORS PREFLIGHT REQUESTS
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                return chain.filter(exchange);
            }

            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // ✅ PUBLIC APIs
            if (path.startsWith("/api/auth")
                    || path.startsWith("/swagger-ui")
                    || path.startsWith("/v3/api-docs")
                    || path.startsWith("/webjars")) {

                return chain.filter(exchange);
            }

            // 🔒 AUTH HEADER REQUIRED
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                System.out.println("NO AUTH HEADER");
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            String authHeader =
                    request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("INVALID AUTH HEADER");
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {

                System.out.println("GATEWAY FILTER HIT");
                System.out.println("PATH = " + path);
                System.out.println("TOKEN = " + token);
                System.out.println("ROLE = " + jwtUtil.extractRole(token));

                if (!jwtUtil.validateToken(token)) {
                    System.out.println("INVALID TOKEN");
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            // ✅ FORWARD HEADERS
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(
                            "X-User-Id",
                            jwtUtil.extractUserId(token).toString()
                    )
                    .header(
                            "X-User-Email",
                            jwtUtil.extractEmail(token)
                    )
                    .header(
                            "X-User-Role",
                            jwtUtil.extractRole(token)
                    )
                    .build();

            System.out.println("HEADERS ADDED");

            return chain.filter(
                    exchange.mutate()
                            .request(modifiedRequest)
                            .build()
            );
        };
    }

    private Mono<Void> onError(
            ServerWebExchange exchange,
            HttpStatus httpStatus
    ) {

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }
}
