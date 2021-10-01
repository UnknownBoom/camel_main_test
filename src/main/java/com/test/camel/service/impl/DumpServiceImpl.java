package com.test.camel.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.camel.xml.jaxb.JaxbHelper.loadRoutesDefinition;

@Service
@RequiredArgsConstructor
@Slf4j
public class DumpServiceImpl implements com.test.camel.service.DumpService {

    private final CamelContext camelContext;
    @Value("${camel.dump.path}")
    private String dumpPath;

    public boolean existsInContext( RouteDefinition routeDefinition) {
        if(isNull(routeDefinition)) return false;
        var routeEndpoints = camelContext.getRoutes().stream()
                .map(t -> t.getEndpoint().getEndpointUri())
                .collect(Collectors.toList());
        return routeEndpoints.contains(routeDefinition.getEndpointUrl());
    }

    @SneakyThrows
    @Override
    public void loadDumpedRoutes() {
        var context = camelContext.adapt(DefaultCamelContext.class);
        var dumpDirectory = new File(dumpPath);
        var files = dumpDirectory.listFiles();
        if (isNull(files) || files.length == 0) {
            log.info("In path {} not found files", dumpDirectory.getPath());
            return;
        }
        FileInputStream is;
        RoutesDefinition routesDefinition;
        for (File file : files) {
            is = new FileInputStream(file);
            routesDefinition = loadRoutesDefinition(camelContext, is);
            var routeDefinition = routesDefinition.getRoutes().get(0);
            if (existsInContext(routeDefinition)) {
                throw new IllegalArgumentException("Route with same endpoint already exists in context");
            }
            context.addRouteDefinition(routeDefinition);
        }
    }

    @SneakyThrows
    @Override
    public void dumpRoutes() {
        var context = camelContext.adapt(DefaultCamelContext.class);
        var modelToXMLDumper = context.getModelToXMLDumper();
        var routeDefinitions = context.getRouteDefinitions();
        String xml;
        File file;
        for (var routeDefinition : routeDefinitions) {
            xml = modelToXMLDumper.dumpModelAsXml(camelContext, routeDefinition);
            file = new File(dumpPath + routeDefinition.getRouteId() + ".xml");
            var isCreatedFile = file.createNewFile();
            if (!isCreatedFile) {
                throw new IOException("File with path not created");
            }
            try (var fileOutputStream = new FileOutputStream(file);) {
                fileOutputStream.write(xml.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.flush();
            }
            log.info("xml: \n{}\n dumped", xml);
        }
    }

}
