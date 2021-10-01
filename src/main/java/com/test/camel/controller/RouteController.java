package com.test.camel.controller;

import com.test.camel.dto.RouteDto;
import com.test.camel.service.DumpService;
import com.test.camel.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/route")
public class RouteController {
    private final RouteService routeService;
    private final DumpService dumpService;

    @PutMapping
    public ResponseEntity<?> updateRoute(@RequestBody RouteDto routeInXml){
        routeService.updateRoute(routeInXml.getRoute());
        return ResponseEntity.ok().build();
    }
    @DeleteMapping
    public ResponseEntity<?> removeRouteByUrl(@RequestBody RouteDto endpoint){
        routeService.removeRouteByEndpoint(endpoint.getEndpoint());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("all")
    public ResponseEntity<?> removeAllRoutes(){
        routeService.removeAllRoutes();
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<?> addNewRoute(@RequestBody RouteDto route){
        routeService.addNewRoute(route.getRoute());
        return ResponseEntity.ok().build();
    }
    @PostMapping("dump")
    public ResponseEntity<?> dumpRoutes(){
        dumpService.dumpRoutes();
        return ResponseEntity.ok().build();
    }

    @PostMapping("dump/load")
    public ResponseEntity<?> loadDumpedRoutes(){
        dumpService.loadDumpedRoutes();
        return ResponseEntity.ok().build();
    }
}
