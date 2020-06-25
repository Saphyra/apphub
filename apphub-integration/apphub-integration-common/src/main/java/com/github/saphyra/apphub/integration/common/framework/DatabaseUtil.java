package com.github.saphyra.apphub.integration.common.framework;

import com.github.saphyra.apphub.integration.common.TestBase;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
public class DatabaseUtil {
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:%s/postgres";

    private static final String ADD_ROLE_BY_EMAIL_QUERY = "INSERT INTO apphub_user.apphub_role (role_id, user_id, apphub_role) VALUES('%s', (SELECT user_id FROM apphub_user.apphub_user WHERE email='%s'), '%s')";
    private static final String GET_USER_ID_BY_EMAIL_QUERY = "SELECT user_id FROM apphub_user.apphub_user WHERE email='%s'";

    public static volatile Connection CONNECTION;

    public static void addRoleByEmail(String email, String... roles) {
        try {
            for (String role : roles) {
                log.info("Adding role {} to email {}", role, email);
                UUID roleId = UUID.randomUUID();
                Statement statement = getStatement();
                String sql = String.format(ADD_ROLE_BY_EMAIL_QUERY, roleId, email, role);
                log.info("AddRole SQL: {}", sql);
                statement.execute(sql);
                statement.close();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static UUID getUserIdByEmail(String email) {
        try {
            Statement statement = getStatement();
            String sql = String.format(GET_USER_ID_BY_EMAIL_QUERY, email);
            log.info("GetUserIdByEmail SQL: {}", sql);

            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return UUID.fromString(resultSet.getString(1));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Statement getStatement() throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        return connection.createStatement();
    }

    private synchronized static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (isNull(CONNECTION)) {
            Class.forName(JDBC_DRIVER);
            CONNECTION = DriverManager.getConnection(String.format(DB_URL, TestBase.DATABASE_PORT), "postgres", "postgres");
        }
        return CONNECTION;
    }
}
