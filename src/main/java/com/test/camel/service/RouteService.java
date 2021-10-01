package com.test.camel.service;

import lombok.NonNull;
import lombok.SneakyThrows;

public interface RouteService {
    @SneakyThrows
    boolean removeRouteById(@NonNull String id);

    @SneakyThrows
    boolean removeRouteByEndpoint(@NonNull String url);

    @SneakyThrows
    void addNewRoute(@NonNull String xml);

    @SneakyThrows
    void updateRoute(@NonNull String xml);

    void removeAllRoutes();
}
