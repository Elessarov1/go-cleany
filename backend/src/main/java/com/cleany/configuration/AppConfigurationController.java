package com.cleany.configuration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/config")
public class AppConfigurationController {

    private final AppConfigurationService configurationService;

    public AppConfigurationController(AppConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping
    public AppConfigurationResponse getConfiguration() {
        return configurationService.getConfiguration();
    }
}

