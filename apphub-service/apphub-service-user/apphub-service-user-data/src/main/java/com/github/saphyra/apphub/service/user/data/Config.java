package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.encryption.impl.PasswordService;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@Configuration
@Slf4j
@EnableJpaRepositories
@EntityScan
class Config {
    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    PasswordService passwordService() {
        return new PasswordService();
    }

    @Bean
    SpringLiquibase liquibase(
        DataSource dataSource,
        @Value("${liquibase.changelog.location}") String changeLogLocation
    ) {
        log.debug("ChangeLogLocation: {}", changeLogLocation);
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(changeLogLocation);
        liquibase.setDataSource(dataSource);
        return liquibase;
    }
}


