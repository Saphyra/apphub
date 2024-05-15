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

        System.exit(0);
    }
}
