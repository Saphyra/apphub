package com.github.saphyra.apphub.ci.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class DatabaseUtil {
    private static final String DB_URL = "jdbc:postgresql://localhost:%s/%s";
    private static final String JDBC_DRIVER = "org.postgresql.Driver";

    @SneakyThrows
    public static Connection getConnection(Integer port, String databaseName) {
        String databaseUrl = String.format(DB_URL, port, databaseName);
        for (int i = 0; i < 10; i++) {
            try {
                return DriverManager.getConnection(databaseUrl, "postgres", "postgres");
            } catch (Exception e) {
                log.info("Failed creating Database connection for port {}. TryCount: {}. Error message: {}", port, i, e.getMessage());
                Thread.sleep(1000);
            }
        }

        throw new RuntimeException("Failed connecting to database on port " + port);
    }

    public static void insertDisabledRoleIfNotPresent(Connection connection, String role) {
        try {
            if (!isRoleAlreadyDisabled(connection, role)) {
                log.info("Disabling role {}...", role);
                insertRole(connection, role);
                log.info("Role {} successfully disabled.", role);
            } else {
                log.info("Role {} is already disabled.", role);
            }
        } catch (Exception e) {
            log.error("Failed inserting disabled role {}", role, e);
        }
    }

    private static void insertRole(Connection connection, String role) throws Exception {
        String sql = "INSERT INTO apphub_user.disabled_role VALUES('%s')".formatted(role);

        execute(connection, sql);
    }

    private static boolean isRoleAlreadyDisabled(Connection connection, String role) throws Exception {
        String sql = "SELECT * from apphub_user.disabled_role WHERE disabled_role='%s'".formatted(role);

        return query(
            connection,
            sql,
            ResultSet::next
        );
    }

    private static <T> T query(Connection connection, String sql, Mapper<T> mapper) throws Exception {
        Class.forName(JDBC_DRIVER);
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);

            T result = mapper.map(resultSet);

            statement.close();

            return result;
        }
    }

    private static void execute(Connection connection, String sql) throws Exception {
        Class.forName(JDBC_DRIVER);

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private interface Mapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
