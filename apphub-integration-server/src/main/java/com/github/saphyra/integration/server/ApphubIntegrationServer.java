package com.github.saphyra.integration.server;

import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static java.util.Objects.isNull;

@SpringBootApplication
@EntityScan
@EnableJpaRepositories
@ComponentScan
@Slf4j
public class ApphubIntegrationServer {
    private static final String CREATE_SCHEMA_SQL = "create schema if not exists %s";

    public static void main(String[] args) {
        SpringApplication.run(ApphubIntegrationServer.class, args);
    }

    @Bean
    SpringLiquibase liquibase(
        DataSource dataSource,
        @Value("${liquibase.changelog.location}") String changeLogLocation,
        @Value("${liquibase.schema}") String schema
    ) {
        log.debug("ChangeLogLocation: {}", changeLogLocation);
        createSchema(dataSource, schema);
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(changeLogLocation);
        liquibase.setDataSource(dataSource);
        liquibase.setLiquibaseSchema(schema);
        return liquibase;
    }

    private void createSchema(DataSource dataSource, String schema) {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = dataSource.getConnection();

            statement = connection.createStatement();
            statement.execute(String.format(CREATE_SCHEMA_SQL, schema));
        } catch (SQLException e) {
            throw new RuntimeException("Schema creation failed.", e);
        } finally {
            try {
                if (!isNull(statement)) {
                    statement.close();
                }
                if (!isNull(connection)) {
                    connection.close();
                }
            } catch (SQLException exception) {
                log.warn("Failed closing connection", exception);
            }
        }
    }
}
