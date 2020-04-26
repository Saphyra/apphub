package com.github.saphyra.apphub.service.platform.web_content;

import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableHealthCheck
public class WebContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebContentApplication.class, args);
    }
}
