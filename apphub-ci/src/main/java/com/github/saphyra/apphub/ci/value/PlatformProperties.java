package com.github.saphyra.apphub.ci.value;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "platform")
@Data
public class PlatformProperties {
    private Integer minikubeProdServerPort;
    private Integer minikubeProdMainGatewayPort;
    private Integer minikubeDevServerPort;
    private Integer minikubeTestServerPort;
    private Integer localServerPort;

    private Integer minikubeDatabasePort;
    private Integer minikubeTestDatabasePort;
    private Integer localDatabasePort;

    private String minikubeDatabaseName;
    private String localDatabaseName;
    private String prodDatabaseName;

    private Service integrationServer;
    private Service productionProxy;

    private List<String> prodDisabledTestGroups;
}
