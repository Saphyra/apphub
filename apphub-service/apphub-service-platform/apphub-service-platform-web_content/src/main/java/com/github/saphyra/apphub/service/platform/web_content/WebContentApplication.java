package com.github.saphyra.apphub.service.platform.web_content;

import com.github.saphyra.apphub.lib.config.common.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.thymeleaf.EnableThymeLeaf;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorTranslation;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableHealthCheck
@EnableLocaleMandatoryRequestValidation
@Import(CommonConfigProperties.class)
@EnableErrorHandler
@EnableThymeLeaf
@EnableErrorTranslation
public class WebContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebContentApplication.class, args);
    }
}
