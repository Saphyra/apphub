package com.github.saphyra.apphub.ci.tool.kubernetes;

import com.github.saphyra.apphub.ci.dao.property.PropertyDao;
import com.github.saphyra.apphub.ci.dao.property.PropertyName;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.Services;
import com.github.saphyra.apphub.ci.value.Technology;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KubernetesNamespaceSetupper {
    private final KubernetesPodStartupWaiter kubernetesPodStartupWaiter;
    private final PropertyDao propertyDao;
    private final Services services;
    private final KubernetesSecretHandler kubernetesSecretHandler;

    public void setupNamespace(Environment environment, String namespaceName, Technology... technologies) {
        createNamespace(namespaceName);
        createSecrets(environment, namespaceName);

        for (Technology technology : technologies) {
            switch (technology) {
                case POSTGRES -> deployPostgres(namespaceName);
                case DYNAMODB -> deployDynamoDb(namespaceName);
                case RABBITMQ -> deployRabbitMq(namespaceName);
            }
        }

        kubernetesPodStartupWaiter.waitForPods(namespaceName, 30); //Greater wait time is required because sometimes the docker images needs to be pulled first
    }

    private void createSecrets(Environment environment, String namespaceName) {
        Map<PropertyName, Map<String, String>> secretsToCreate = services.getServices()
            .stream()
            .flatMap(service -> service.getProperties().stream())
            .distinct()
            .collect(Collectors.toMap(
                propertyName -> propertyName,
                propertyName -> propertyDao.getEnvironmentSpecificProperties(propertyName)
                    .getForEnvironmentOrDefault(environment))
            );

        secretsToCreate.forEach((propertyName, secretData) -> kubernetesSecretHandler.create(namespaceName, propertyName.name().toLowerCase().replace("_", "-"), secretData));
    }

    @SneakyThrows
    public void createNamespace(String namespaceName) {
        new ProcessBuilder("kubectl", "create", "namespace", namespaceName)
            .inheritIO()
            .start()
            .waitFor();
    }

    @SneakyThrows
    private void deployPostgres(String namespaceName) {
        new ProcessBuilder("kubectl", "apply", "-n", namespaceName, "-f", "infra/deploy-postgres.yaml")
            .inheritIO()
            .start()
            .waitFor();
    }

    @SneakyThrows
    private void deployDynamoDb(String namespaceName) {
        new ProcessBuilder("kubectl", "apply", "-n", namespaceName, "-f", "infra/deploy-dynamo-db.yaml")
            .inheritIO()
            .start()
            .waitFor();
    }

    @SneakyThrows
    private void deployRabbitMq(String namespaceName) {
        new ProcessBuilder("kubectl", "apply", "-n", namespaceName, "-f", "infra/deploy-rabbitmq.yaml")
            .inheritIO()
            .start()
            .waitFor();
    }
}
