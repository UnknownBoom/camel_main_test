package com.test.camel.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.engine.AbstractCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.camel.xml.jaxb.JaxbHelper.loadRoutesDefinition;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService implements com.test.camel.service.RouteService {
    private final CamelContext camelContext;

    public boolean existsInContext(RouteDefinition routeDefinition) {
        if (isNull(routeDefinition)) return false;
        var routeEndpoints = camelContext.getRoutes().stream()
                .map(t -> t.getEndpoint().getEndpointUri())
                .collect(Collectors.toList());
        return routeEndpoints.contains(routeDefinition.getEndpointUrl());
    }

    @SneakyThrows
    @Override
    public boolean removeRouteById(@NonNull String id) {
        var context = camelContext.adapt(AbstractCamelContext.class);
        context.stopRoute(id);
        return context.removeRoute(id);
    }

    @SneakyThrows
    @Override
    public boolean removeRouteByEndpoint(@NonNull String url) {
        var context = camelContext.adapt(AbstractCamelContext.class);
        var routes = camelContext.getRoutes().stream()
                .filter(t -> url.equals(t.getEndpoint().getEndpointUri()))
                .collect(Collectors.toList());
        if (routes.isEmpty()) {
            throw new IllegalArgumentException("Route not found");
        }
        context.stopRoute(routes.get(0).getRouteId());
        return camelContext.removeRoute(routes.get(0).getRouteId());

    }

    @Override
    @SneakyThrows
    public void addNewRoute(@NonNull String xml) {
        var context = camelContext.adapt(DefaultCamelContext.class);
        var newRoutesDefinition = loadRoutesDefinition(camelContext, new ByteArrayInputStream(xml.getBytes()));
        var newRouteDefinition = newRoutesDefinition.getRoutes().get(0);
        if (existsInContext(newRouteDefinition)) {
            throw new IllegalArgumentException("Duplicate route endpoint");
        }
        // replace route with same id
        context.addRouteDefinition(newRouteDefinition);
    }

    @Override
    @SneakyThrows
    public void updateRoute(@NonNull String xml) {
        var context = camelContext.adapt(DefaultCamelContext.class);
        var newRoutesDefinition = loadRoutesDefinition(camelContext, new ByteArrayInputStream(xml.getBytes()));
        var newRouteDefinition = newRoutesDefinition.getRoutes().get(0);
        if (existsInContext(newRouteDefinition)) {
            removeRouteByEndpoint(newRouteDefinition.getEndpointUrl());
        }
        context.addRouteDefinition(newRouteDefinition);
    }

    @Override
    @SneakyThrows
    public void removeAllRoutes() {
        var context = camelContext.adapt(DefaultCamelContext.class);
        context.removeRouteDefinitions(new ArrayList(context.getRouteDefinitions()));
    }
}
