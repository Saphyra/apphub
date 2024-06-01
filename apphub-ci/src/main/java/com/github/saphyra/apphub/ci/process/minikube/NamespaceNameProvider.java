package com.github.saphyra.apphub.ci.process.minikube;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class NamespaceNameProvider {
    @SneakyThrows
    public String getBranchName() {
        Process process = Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD");
        process.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        return reader.readLine();
    }
}
