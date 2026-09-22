package com.github.saphyra.apphub.ci.value;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "platform")
@Data
//TODO use constant values instead of reading from config
//TODO check naming
public class PlatformProperties {
    private Integer minikubePreprodServerPort;
    private Integer minikubeProdServerPort;
    private Integer minikubePreprodMainGatewayPort; //preprod-proxy proxies this port
    private Integer minikubeProdMainGatewayPort; //production-proxy proxies this port
    private Integer minikubeDevServerPort;
    private Integer minikubeTestServerPort;
    private Integer localServerPort;
    private Integer minikubeDynamoDbPort;

    private Integer minikubeDatabasePort;
    private Integer minikubeTestDatabasePort;
    private Integer localDatabasePort;
    private Integer localDynamoDbPort;

    private String minikubeDatabaseName;
    private String localDatabaseName;
    private String prodDatabaseName;

    private Service integrationServer;
    private Service productionProxy;
    private Service preprodProxy;

    private List<String> prodDisabledTestGroups;
    private List<String> prodDisabledRoles;
}
