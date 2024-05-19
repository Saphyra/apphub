package com.github.saphyra.apphub.ci.value;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@ConfigurationProperties(prefix = "")
@Data
@Configuration
public class Services {
    private List<Service> services;

    public Optional<Service> findByName(String serviceName) {
        return services.stream()
            .filter(service -> service.getName().equals(serviceName))
            .findFirst();
    }

    public Service findByNameValidated(String serviceName) {
        return findByName(serviceName)
            .orElseThrow(() -> new RuntimeException("Service not found by name " + serviceName));
    }
}
