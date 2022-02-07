package com.github.saphyra.apphub.integration.common.framework;

import com.github.saphyra.apphub.integration.common.TestBase;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
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
    private static final String DB_URL = "jdbc:postgresql://localhost:%s/postgres";

    private static final String ADD_ROLE_BY_EMAIL_QUERY = "INSERT INTO apphub_user.apphub_role (role_id, user_id, apphub_role) VALUES('%s', (SELECT user_id FROM apphub_user.apphub_user WHERE email='%s'), '%s')";
    private static final String GET_USER_ID_BY_EMAIL_QUERY = "SELECT user_id FROM apphub_user.apphub_user WHERE email='%s'";
    private static final String GET_ROLES_BY_USER_ID = "SELECT apphub_role FROM apphub_user.apphub_role WHERE user_id='%s'";
    private static final String UPDATE_ACCESS_TOKEN_LAST_ACCESS = "UPDATE apphub_user.access_token SET last_access='%s' WHERE access_token_id='%s'";
    private static final String FIND_SKYXPLORE_CHARACTER_NAME_BY_EMAIL = "SELECT name FROM skyxplore.character WHERE user_id = (SELECT user_id FROM apphub_user.apphub_user where email='%s')";
    private static final String SET_MARKED_FOR_DELETION_AT_BY_EMAIL = "UPDATE apphub_user.apphub_user SET marked_for_deletion_at='%s' WHERE email='%s'";
    private static final String SET_MARKED_FOR_DELETION_BY_EMAIL_LIKE = "UPDATE apphub_user.apphub_user SET marked_for_deletion=true WHERE email LIKE '%s'";

    private static <T> T query(String sql, Mapper<T> mapper) throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        T result = mapper.map(resultSet);

        statement.close();
        connection.close();

        return result;
    }

    private static void execute(String sql) throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        statement.execute(sql);

        statement.close();
        connection.close();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(String.format(DB_URL, TestBase.DATABASE_PORT), "postgres", "postgres");
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
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
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

        } catch (SQLException | ClassNotFoundException e) {
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
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateAccessTokenLastAccess(UUID accessTokenId, LocalDateTime newLastAccess) {
        String sql = String.format(UPDATE_ACCESS_TOKEN_LAST_ACCESS, newLastAccess, accessTokenId);
        log.debug("updateAccessTokenLastAccess sql: {}", sql);

        try {
            execute(sql);
        } catch (ClassNotFoundException | SQLException e) {
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
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed querying SkyXploreCharacter name", e);
        }
    }

    public static void setMarkedForDeletionAtByEmail(String email, LocalDateTime date) {
        String sql = String.format(SET_MARKED_FOR_DELETION_AT_BY_EMAIL, date, email);

        try {
            execute(sql);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed setting markedForDeletionAt", e);
        }
    }

    public static void setMarkedForDeletionByEmailLike(String emailDomain) {
        String sql = String.format(SET_MARKED_FOR_DELETION_BY_EMAIL_LIKE, "%" + emailDomain + "%");

        try {
            execute(sql);
        } catch (ClassNotFoundException | SQLException e) {
            log.error("setMarkedForDeletionByEmailLike failed", e);
            throw new RuntimeException("Failed setting markedForDeletion", e);
        }
    }

    private interface Mapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
