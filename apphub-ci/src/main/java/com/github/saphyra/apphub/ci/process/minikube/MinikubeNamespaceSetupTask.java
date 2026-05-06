package com.github.saphyra.apphub.ci.process.minikube;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MinikubeNamespaceSetupTask {
    private final MinikubePodStartupWaiter minikubePodStartupWaiter;
    private final PropertyDao propertyDao;
    private final Services services;
    private final MinikubeSecretService minikubeSecretService;

    public void setupNamespace(Environment environment, String namespaceName) {
        createNamespace(namespaceName);
        createSecrets(environment, namespaceName);
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

        secretsToCreate.forEach((propertyName, secretData) -> minikubeSecretService.create(namespaceName, propertyName.name().toLowerCase().replace("_", "-"), secretData));
    }

    @SneakyThrows
    public void createNamespace(String namespaceName) {
        new ProcessBuilder("kubectl", "create", "namespace", namespaceName)
            .inheritIO()
            .start()
            .waitFor();
    }

    @SneakyThrows
    public void deployPostgres(String namespaceName) {
        new ProcessBuilder("kubectl", "apply", "-n", namespaceName, "-f", "infra/persistent-volume.yaml")
            .inheritIO()
            .start()
            .waitFor();

        new ProcessBuilder("kubectl", "apply", "-n", namespaceName, "-f", "infra/deploy-postgres.yaml")
            .inheritIO()
            .start()
            .waitFor();

        minikubePodStartupWaiter.waitForPods(namespaceName, 10);
    }

    @SneakyThrows
    public void deployDynamoDb(String namespaceName) {
        new ProcessBuilder("kubectl", "apply", "-n", namespaceName, "-f", "infra/deploy-dynamo-db.yaml")
            .inheritIO()
            .start()
            .waitFor();

        minikubePodStartupWaiter.waitForPods(namespaceName, 10);
    }
}
