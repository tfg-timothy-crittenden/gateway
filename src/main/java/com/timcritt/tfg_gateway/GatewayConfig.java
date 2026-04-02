package com.timcritt.tfg_gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("material-service", r -> r
                        .path("/materials/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://EXAM-SERVICE"))
                .route("user-service", r -> r
                        .path("/users/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://USER-SERVICE"))
                .route("classroom-service", r -> r
                        .path("/classrooms/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://CLASSROOM-SERVICE"))
                .build()
                ;

    }
}

