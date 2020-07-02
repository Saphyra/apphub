package com.github.saphyra.apphub.service.notebook.config;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenConfiguration;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.liquibase.EnableLiquibase;
import com.github.saphyra.apphub.lib.config.thymeleaf.EnableThymeLeaf;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocalMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import com.github.saphyra.apphub.lib.security.role.RoleFilterConfiguration;
import com.github.saphyra.apphub.service.notebook.NotebookApplication;
import com.github.saphyra.encryption.EnableEncryption;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration

@EnableJpaRepositories(basePackageClasses = NotebookApplication.class)
@EntityScan(basePackageClasses = NotebookApplication.class)
@ComponentScan(basePackages = "com.github.saphyra.util")
@EnableLiquibase

@EnableHealthCheck
@EnableThymeLeaf
@EnableErrorHandler
@EnableLocalMandatoryRequestValidation
@EnableEncryption
@Import({
    CommonConfigProperties.class,
    AccessTokenConfiguration.class,
    AccessTokenFilterConfiguration.class,
    RoleFilterConfiguration.class
})
public class BeanConfiguration {

    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }
}
