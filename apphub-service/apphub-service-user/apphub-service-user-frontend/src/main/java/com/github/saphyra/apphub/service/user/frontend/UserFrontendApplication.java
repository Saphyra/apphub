package com.github.saphyra.apphub.service.user.frontend;

import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.thymeleaf.EnableThymeLeaf;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableThymeLeaf
@EnableHealthCheck
public class UserFrontendApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserFrontendApplication.class, args);
    }
}
