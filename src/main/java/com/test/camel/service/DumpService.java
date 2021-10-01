package com.test.camel.service;

import lombok.SneakyThrows;

public interface DumpService {
    @SneakyThrows
    void loadDumpedRoutes();

    @SneakyThrows
    void dumpRoutes();
}
