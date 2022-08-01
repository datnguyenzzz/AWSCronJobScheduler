package com.github.datnguyenzzz.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.datnguyenzzz.Factories.ProviderFactory;
import com.github.datnguyenzzz.Interfaces.CronJobProvider;

@RestController
@RequestMapping("/")
@EnableAutoConfiguration
public class WebController {

    @Autowired
    private ProviderFactory providerFactory;

    @GetMapping
    public String index() throws Exception {
        CronJobProvider provider = providerFactory.getObject();
        provider.getDefinition();
        return "Okay!!";
    }
}
