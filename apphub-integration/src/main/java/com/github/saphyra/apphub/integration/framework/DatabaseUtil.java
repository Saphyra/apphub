package com.github.saphyra.apphub.integration.framework;

import com.github.saphyra.apphub.integration.core.connection.ConnectionProvider;
import com.github.saphyra.apphub.integration.core.util.AutoCloseableImpl;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class DatabaseUtil {
    private static final String JDBC_DRIVER = "org.postgresql.Driver";

    private static final String FIND_SKYXPLORE_CHARACTER_NAME_BY_USER_ID = "SELECT name FROM skyxplore.character WHERE user_id = '%s'";
    private static final String INSERT_MIGRATION_TASK = "INSERT INTO admin_panel.migration_task(event, name, completed, repeatable) VALUES ('%s', '%s', '%s', '%s');";
    private static final String DELETE_MIGRATION_TASK_BY_EVENT = "DELETE FROM admin_panel.migration_task WHERE event='%s'";
    private static final String GET_ROW_COUNT_BY_USER_ID = "SELECT count(*) from %s.%s WHERE %s='%s'";

    private static <T> T query(String sql, Mapper<T> mapper) throws Exception {
        Class.forName(JDBC_DRIVER);
        try (AutoCloseableImpl<Connection> conn = ConnectionProvider.getDatabaseConnection()) {
            Connection connection = conn.getObject();
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(sql);

                T result = mapper.map(resultSet);

                statement.close();

                return result;
            }
        }
    }

    private static void execute(String sql) throws Exception {
        Class.forName(JDBC_DRIVER);

        try (AutoCloseableImpl<Connection> conn = ConnectionProvider.getDatabaseConnection()) {
            Connection connection = conn.getObject();
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }

    public static void insertMigrationTask(String event, String name, boolean completed, boolean repeatable) {
        try {
            String sql = INSERT_MIGRATION_TASK.formatted(event, name, completed, repeatable);
            execute(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed inserting new Migration Task", e);
        }
    }

    public static void deleteMigrationTaskByEvent(String event) {
        try {
            String sql = DELETE_MIGRATION_TASK_BY_EVENT.formatted(event);
            execute(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed deleting MigrationTask by event " + event, e);
        }
    }

    public static Optional<String> findSkyXploreCharacterByUserId(UUID userId) {
        String sql = String.format(FIND_SKYXPLORE_CHARACTER_NAME_BY_USER_ID, userId);

        try {
            return query(
                sql,
                rs -> {
                    if (rs.next()) {
                        return Optional.of(rs.getString(1));
                    } else {
                        return Optional.empty();
                    }
                }
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed querying SkyXploreCharacter name", e);
        }
    }

    public static Integer getRowCountByValue(UUID userId, String schema, String tableName, String column) {
        String sql = GET_ROW_COUNT_BY_USER_ID.formatted(schema, tableName, column, userId);

        try {
            return query(
                sql,
                rs -> {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }

                    throw new IllegalStateException("Failed querying row count from table %s.%s for user_id %s".formatted(schema, tableName, userId));
                }
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed querying row count from table %s.%s for user_id %s".formatted(schema, tableName, userId), e);
        }
    }

    private interface Mapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
