package com.github.saphyra.apphub.ci;

import com.github.saphyra.apphub.ci.menu.main_menu.MainMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@RequiredArgsConstructor
@ComponentScan
@EntityScan
@EnableJpaRepositories
@EnableAutoConfiguration
@EnableConfigurationProperties
public class ApphubCiApplication implements CommandLineRunner {
    private final MainMenu mainMenu;

    public static void main(String[] args) {
        SpringApplication.run(ApphubCiApplication.class, args);
    }

    @Override
    public void run(String... args) {
        mainMenu.enter();
    }

    private static void process() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("cmd /c mvn -T 12 clean package -DskipTests");

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Maven build successful!");
        } else {
            System.out.println("Maven build failed!");
        }
    }
}
