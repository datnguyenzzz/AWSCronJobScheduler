package com.github.datnguyenzzz.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.datnguyenzzz.Components.HealthCheckHandler;
import com.github.datnguyenzzz.dto.HealthStatus;

@RestController
public class WebController {

    @Autowired
    private HealthCheckHandler healthCheckHandler;

    @GetMapping("/")
    public String index() throws Exception {
        return "Okay!!";
    }

    @GetMapping("/hs")
    public List<HealthStatus> getAllHealthStatus() {
        Map<Integer, HealthStatus> mapHS = this.healthCheckHandler.getAllHealthStatus();
        List<HealthStatus> listAllHS = mapHS.entrySet().stream().map(x -> x.getValue()).collect(Collectors.toList());
        return listAllHS;
    } 
}
