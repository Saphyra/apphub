package com.github.saphyra.apphub.integration.framework;

import com.github.saphyra.apphub.integration.core.connection.ConnectionProvider;
import com.github.saphyra.apphub.integration.core.util.AutoCloseableImpl;
import com.github.saphyra.apphub.integration.structure.api.calendar.Occurrence;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class DatabaseUtil {
    private static final String JDBC_DRIVER = "org.postgresql.Driver";

    private static final String ADD_ROLE_BY_EMAIL_QUERY = "INSERT INTO apphub_user.apphub_role (role_id, user_id, apphub_role) VALUES('%s', (SELECT user_id FROM apphub_user.apphub_user WHERE email='%s'), '%s')";
    private static final String REMOVE_ROLE_BY_EMAIL_QUERY = "DELETE FROM apphub_user.apphub_role WHERE user_id=(SELECT user_id FROM apphub_user.apphub_user WHERE email='%s') AND apphub_role='%s'";
    private static final String GET_USER_ID_BY_EMAIL_QUERY = "SELECT user_id FROM apphub_user.apphub_user WHERE email='%s'";
    private static final String GET_ROLES_BY_USER_ID = "SELECT apphub_role FROM apphub_user.apphub_role WHERE user_id='%s'";
    private static final String UPDATE_ACCESS_TOKEN_LAST_ACCESS = "UPDATE apphub_user.access_token SET last_access='%s' WHERE access_token_id='%s'";
    private static final String FIND_SKYXPLORE_CHARACTER_NAME_BY_EMAIL = "SELECT name FROM skyxplore.character WHERE user_id = (SELECT user_id FROM apphub_user.apphub_user where email='%s')";
    private static final String SET_MARKED_FOR_DELETION_AT_BY_EMAIL = "UPDATE apphub_user.apphub_user SET marked_for_deletion_at='%s' WHERE email='%s'";
    private static final String SET_MARKED_FOR_DELETION_BY_EMAIL_LIKE = "UPDATE apphub_user.apphub_user SET marked_for_deletion=true WHERE email LIKE '%s'";
    private static final String GET_CALENDAR_OCCURRENCES_BY_USER_ID = "SELECT * FROM calendar.occurrence WHERE user_id='%s'";
    private static final String UNLOCK_USER_BY_EMAIL = "UPDATE apphub_user.apphub_user SET locked_until = null WHERE email='%s'";
    private static final String INSERT_MIGRATION_TASK = "INSERT INTO admin_panel.migration_task(event, name, completed) VALUES ('%s', '%s', '%s');";
    private static final String DELETE_MIGRATION_TASK_BY_EVENT = "DELETE FROM admin_panel.migration_task WHERE event='%s'";
    private static final String GET_ROLE_COUNT = "SELECT count(*) from apphub_user.apphub_role WHERE apphub_role='%s'";
    private static final String GET_ENCRYPTED_DATA_FROM_CHECKED_ITEM = "SELECT checked FROM notebook.checked_item WHERE user_id='%s' LIMIT 1";
    private static final String INJECT_ENCRYPTED_DATA_TO_MODULES = "UPDATE modules.favorite SET favorite='%s' WHERE user_id='%s'";
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

    @SneakyThrows
    public static int getRoleCount(String role) {
        String sql = GET_ROLE_COUNT.formatted(role);
        return query(sql, rs -> {
            rs.next();
            return rs.getInt(1);
        });
    }

    @SneakyThrows
    public static void removeRoleByEmail(String email, String... roles) {
        for (String role : roles) {
            log.debug("Removing role {} from email {}", role, email);
            String sql = String.format(REMOVE_ROLE_BY_EMAIL_QUERY, email, role);
            log.debug("RemoveRole SQL: {}", sql);
            execute(sql);
        }
    }

    public static void addRoleByEmail(String email, String... roles) {
        try {
            for (String role : roles) {
                log.debug("Adding role {} to email {}", role, email);
                UUID roleId = UUID.randomUUID();
                String sql = String.format(ADD_ROLE_BY_EMAIL_QUERY, roleId, email, role);
                log.debug("AddRole SQL: {}", sql);
                execute(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertMigrationTask(String event, String name, boolean completed) {
        try {
            String sql = INSERT_MIGRATION_TASK.formatted(event, name, completed);
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

    public static UUID getUserIdByEmail(String email) {
        try {
            String sql = String.format(GET_USER_ID_BY_EMAIL_QUERY, email);
            log.debug("GetUserIdByEmail SQL: {}", sql);

            UUID result = query(sql, rs -> {
                rs.next();
                return UUID.fromString(rs.getString(1));
            });
            log.debug("UserId for email {}: {}", result, email);
            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getRolesByUserId(UUID userId) {
        try {
            String sql = String.format(GET_ROLES_BY_USER_ID, userId);
            log.debug("GetRolesByUserId SQL: {}", sql);

            return query(
                sql,
                rs -> {
                    List<String> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(rs.getString("apphub_role"));
                    }
                    return result;
                }
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateAccessTokenLastAccess(UUID accessTokenId, LocalDateTime newLastAccess) {
        String sql = String.format(UPDATE_ACCESS_TOKEN_LAST_ACCESS, newLastAccess, accessTokenId);
        log.debug("updateAccessTokenLastAccess sql: {}", sql);

        try {
            execute(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed updating accessToken lastAccess", e);
        }
    }

    public static Optional<String> findSkyXploreCharacterByEmail(String email) {
        String sql = String.format(FIND_SKYXPLORE_CHARACTER_NAME_BY_EMAIL, email);

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

    public static void setMarkedForDeletionAtByEmail(String email, LocalDateTime date) {
        String sql = String.format(SET_MARKED_FOR_DELETION_AT_BY_EMAIL, date, email);

        try {
            execute(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed setting markedForDeletionAt", e);
        }
    }

    public static void setMarkedForDeletionByEmailLike(String emailDomain) {
        String sql = String.format(SET_MARKED_FOR_DELETION_BY_EMAIL_LIKE, "%" + emailDomain + "%");

        try {
            execute(sql);
        } catch (Exception e) {
            log.error("setMarkedForDeletionByEmailLike failed", e);
            throw new RuntimeException("Failed setting markedForDeletion", e);
        }
    }

    public static List<Occurrence> getCalendarOccurrencesByUserId(UUID userId) {
        String sql = String.format(GET_CALENDAR_OCCURRENCES_BY_USER_ID, userId);

        try {
            return query(
                sql,
                rs -> {
                    List<Occurrence> result = new ArrayList<>();
                    while (rs.next()) {
                        Occurrence occurrence = Occurrence.builder()
                            .occurrenceId(UUID.fromString(rs.getString("occurrence_id")))
                            .eventId(UUID.fromString(rs.getString("event_id")))
                            .userId(UUID.fromString(rs.getString("user_id")))
                            .date(rs.getString("date"))
                            .status(rs.getString("status"))
                            .note(rs.getString("note"))
                            .build();
                        result.add(occurrence);
                    }
                    return result;
                }
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed querying Calendar occurrences", e);
        }
    }

    public static void unlockUserByEmail(String email) {
        try {
            String sql = String.format(UNLOCK_USER_BY_EMAIL, email);
            execute(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed unlocking user.", e);
        }
    }

    public static String getEncryptedDataFromCheckedItem(UUID userId) {
        String sql = GET_ENCRYPTED_DATA_FROM_CHECKED_ITEM.formatted(userId);

        try {
            return query(
                sql,
                rs -> {
                    if (rs.next()) {
                        return rs.getString("checked");
                    }

                    throw new IllegalStateException("No checked item found by userId " + userId);
                }
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed querying encrypted data from checked_item", e);
        }
    }

    public static void injectEncryptedDataToModules(UUID userId, String data) {
        String sql = INJECT_ENCRYPTED_DATA_TO_MODULES.formatted(data, userId);

        try {
            execute(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed injecting data to modules.", e);
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
