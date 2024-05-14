package com.github.saphyra.apphub.ci.value;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "")
@Data
@Configuration
public class Services {
    private List<Service> services;
}
