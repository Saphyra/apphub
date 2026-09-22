package com.github.saphyra.apphub.ci.value;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@NoArgsConstructor
public class PlatformProperties {
    /**
     * Preprod proxy listens on this port.
     */
    private final int minikubePreprodServerPort = 8999;

    /**
     * Production proxy listens on this port.
     */
    private final int minikubeProdServerPort = 9000;

    /**
     * Preprod's main-gateway is forwarded to this port. Preprod proxy proxies this port.
     */
    private final int minikubePreprodMainGatewayPort = 8059;

    /**
     * Production's main-gateway is forwarded to this port. Production proxy proxies this port.
     */
    private final int minikubeProductionMainGatewayPort = 8060;

    /**
     * Minikube dev server's main-gateway is forwarded to this port.
     */
    private final int minikubeDevServerPort = 9001;

    /**
     * Port main-gateway's pod listens to. Integration test's ConnectionProvider will forward this port to a random port on localhost.
     */
    private final int minikubeMainGatewayPort = 8080;

    /**
     * Local server's main-gateway listens to this port
     */
    private final int localServerPort = 8080;

    /**
     * Minikube dev server's DynamoDB is forwarded to this port.
     */
    private final int minikubeDynamoDbPort = 9003;

    /**
     * Minikube dev server's database is forwarded to this port.
     */
    private final int minikubeDatabasePort = 9002;

    /**
     * Minikube dev server's PSQL pod listens to this port
     */
    private final int minikubeTestDatabasePort = 5432;

    /**
     * Local PSQL database listens to this port
     */
    private final int localDatabasePort = 5432;

    /**
     * Local DynamoDB listens to this port
     */
    private final int localDynamoDbPort = 8000;

    /**
     * Minikube dev server's database name
     */
    private final String minikubeDatabaseName = "postgres";

    /**
     * Local server's database name
     */
    private final String localDatabaseName = "apphub";

    /**
     * Preprod server's database name
     */
    private final String preprodDatabaseName = "apphub_preprod";

    /**
     * Production server's database name
     */
    private final String prodDatabaseName = "apphub_production";

    private final Service integrationServer = Service.builder()
        .name("integration-server")
        .port(8072)
        .location("./apphub-integration-server/target/application.jar")
        .moduleName("apphub-integration-server")
        .build();
    private final Service productionProxy = Service.builder()
        .name("production-proxy")
        .port(9000)
        .location("./apphub-proxy/target/proxy.jar")
        .healthCheckPort(8998)
        .build();
    private final Service preprodProxy = Service.builder()
        .name("preprod-proxy")
        .port(8999)
        .location("./apphub-proxy/target/proxy.jar")
        .healthCheckPort(8997)
        .build();

    private final List<String> prodDisabledTestGroups = List.of(
        "skyxplore",
        "community",
        "task-manager"
    );
    private final List<String> prodDisabledRoles = List.of(
        "SKYXPLORE",
        "COMMUNITY",
        "TASK_MANAGER"
    );
}
