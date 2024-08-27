package com.github.saphyra.apphub.integration.core.connection;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class DatabasePortConnectionFactory implements PooledObjectFactory<BiWrapper<Connection, Integer>> {
    private static final String DB_URL = "jdbc:postgresql://localhost:%s/%s";

    public static GenericObjectPool<BiWrapper<Connection, Integer>> getInstance() {
        GenericObjectPoolConfig<BiWrapper<Connection, Integer>> config = new GenericObjectPoolConfig<>();
        config.setTestOnBorrow(true);
        config.setMaxWaitMillis(10000);
        config.setMaxTotal(20);

        return new GenericObjectPool<>(new DatabasePortConnectionFactory(), config);
    }

    @Override
    public PooledObject<BiWrapper<Connection, Integer>> makeObject() throws Exception {
        Integer port;

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            port = serverSocket.getLocalPort();
        }

        PortForwardTask.portForwardDatabase(port);
        log.debug("DatabasePort {} forwarded", port);
        Connection connection = getConnection(port);
        log.debug("Database connection created");

        return new DefaultPooledObject<>(new BiWrapper<>(connection, port));
    }

    public static Connection getConnection(Integer port) throws SQLException {
        String databaseUrl = String.format(DB_URL, port, TestConfiguration.DATABASE_NAME);
        return DriverManager.getConnection(databaseUrl, "postgres", "postgres");
    }

    @Override
    public void destroyObject(PooledObject<BiWrapper<Connection, Integer>> p) throws Exception {
        BiWrapper<Connection, Integer> biWrapper = p.getObject();

        biWrapper.getEntity1().close();
        ProcessKiller.killByPort(biWrapper.getEntity2());

        log.info("DatabasePort {} terminated.", biWrapper.getEntity2());
    }

    @Override
    public boolean validateObject(PooledObject<BiWrapper<Connection, Integer>> p) {
        try {
            Connection connection = p.getObject()
                .getEntity1();
            validateConnection(connection);

            return true;
        } catch (Exception e) {
            log.warn("DatabasePort {} is invalid", p.getObject().getEntity2(), e);
            return false;
        }
    }

    private static void validateConnection(Connection connection) throws SQLException {
        log.debug("Validating database connection...");
        try (Statement statement = connection.createStatement()) {
            statement.executeQuery("SELECT count(1)");
        }
    }

    @Override
    public void activateObject(PooledObject<BiWrapper<Connection, Integer>> p) {

    }

    @Override
    public void passivateObject(PooledObject<BiWrapper<Connection, Integer>> p) {

    }
}
