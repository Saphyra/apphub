package com.github.saphyra.apphub.ci.value;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "test")
@Configuration
@Data
public class TestProperties {
    private Service integrationServer;
    private Integer localServerPort;
    private Integer localDatabasePort;
    private String  localDatabaseName;
}
