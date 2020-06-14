package com.github.saphyra.apphub.service.platform.web_content;

import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocalMandatoryRequestValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableHealthCheck
@EnableLocalMandatoryRequestValidation
@Import(CommonConfigProperties.class)
@EnableErrorHandler
public class WebContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebContentApplication.class, args);
    }
}
