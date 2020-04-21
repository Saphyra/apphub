package com.github.saphyra.apphub.test.common.repository;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@TestConfiguration
@ImportAutoConfiguration(value = HibernateJpaAutoConfiguration.class)
@EnableJpaRepositories(basePackages = "com.github.saphyra.apphub")
@EntityScan(basePackages = "com.github.saphyra.apphub")
public class RepositoryTestConfiguration {
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }

    @Bean
    public SpringLiquibase liquibase(
        DataSource dataSource,
        @Value("classpath:database/changelog-master.xml") String changeLogLocation
    ) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(changeLogLocation);
        liquibase.setDataSource(dataSource);
        return liquibase;
    }
}
