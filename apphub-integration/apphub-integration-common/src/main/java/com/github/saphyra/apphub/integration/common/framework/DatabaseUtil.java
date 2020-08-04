package com.github.saphyra.apphub.integration.common.framework;

import com.github.saphyra.apphub.integration.common.TestBase;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class DatabaseUtil {
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:%s/postgres";

    private static final String ADD_ROLE_BY_EMAIL_QUERY = "INSERT INTO apphub_user.apphub_role (role_id, user_id, apphub_role) VALUES('%s', (SELECT user_id FROM apphub_user.apphub_user WHERE email='%s'), '%s')";
    private static final String GET_USER_ID_BY_EMAIL_QUERY = "SELECT user_id FROM apphub_user.apphub_user WHERE email='%s'";
    private static final String GET_ROLES_BY_USER_ID = "SELECT apphub_role FROM apphub_user.apphub_role WHERE user_id='%s'";

    private static <T> T query(String sql, Mapper<T> mapper) throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection(String.format(DB_URL, TestBase.DATABASE_PORT), "postgres", "postgres");
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        T result = mapper.map(resultSet);

        statement.close();
        connection.close();

        return result;
    }

    private static void execute(String sql) throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection(String.format(DB_URL, TestBase.DATABASE_PORT), "postgres", "postgres");
        Statement statement = connection.createStatement();

        statement.execute(sql);

        statement.close();
        connection.close();
    }

    public static void addRoleByEmail(String email, String... roles) {
        try {
            for (String role : roles) {
                log.info("Adding role {} to email {}", role, email);
                UUID roleId = UUID.randomUUID();
                String sql = String.format(ADD_ROLE_BY_EMAIL_QUERY, roleId, email, role);
                log.info("AddRole SQL: {}", sql);
                execute(sql);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static UUID getUserIdByEmail(String email) {
        try {
            String sql = String.format(GET_USER_ID_BY_EMAIL_QUERY, email);
            log.info("GetUserIdByEmail SQL: {}", sql);

            return query(sql, rs -> {
                rs.next();
                return UUID.fromString(rs.getString(1));
            });

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getRolesByUserId(UUID userId) {
        try {
            String sql = String.format(GET_ROLES_BY_USER_ID, userId);
            log.info("GetRolesByUserId SQL: {}", sql);

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

    private interface Mapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
