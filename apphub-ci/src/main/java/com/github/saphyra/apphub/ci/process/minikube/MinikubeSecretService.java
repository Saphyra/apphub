package com.github.saphyra.apphub.ci.process.minikube;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinikubeSecretService {
    @SneakyThrows
    public void create(String namespaceName, String secretName, Map<String, String> secretData) {
        deleteSecret(namespaceName, secretName);

        List<String> commands = new ArrayList<>();
        commands.addAll(List.of(
            "kubectl",
            "create",
            "secret",
            "-n",
            namespaceName,
            "generic",
            secretName
        ));
        commands.addAll(getDataCommands(secretData));

        new ProcessBuilder(commands)
            .inheritIO()
            .start()
            .waitFor();
    }

    private Collection<String> getDataCommands(Map<String, String> secretData) {
        return secretData.entrySet()
            .stream()
            .map(entry -> "--from-literal=%s=%s".formatted(entry.getKey(), entry.getValue()))
            .toList();
    }

    private static void deleteSecret(String namespaceName, String secretName) throws InterruptedException, IOException {
        new ProcessBuilder("kubectl", "delete", "secret", "-n", namespaceName, secretName, "--ignore-not-found")
            .inheritIO()
            .start()
            .waitFor();
    }
}
