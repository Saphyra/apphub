package com.github.saphyra.apphub.service.user.frontend;

import com.github.saphyra.apphub.lib.config.thymeleaf.EnableThymeLeaf;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableThymeLeaf
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
