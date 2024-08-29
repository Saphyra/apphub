package com.github.saphyra.apphub.integration.core.connection;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import com.github.saphyra.apphub.integration.core.util.AutoCloseableImpl;
import com.github.saphyra.apphub.integration.core.util.CacheItemWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.sql.Connection;

import static java.util.Objects.isNull;

@Slf4j
public class ConnectionProvider {
    private static final GenericObjectPool<CacheItemWrapper<Integer>> SERVER_CONNECTION_CACHE = ServerPortConnectionFactory.getInstance();
    private static final GenericObjectPool<BiWrapper<Connection, Integer>> DATABASE_CONNECTION_CACHE = DatabasePortConnectionFactory.getInstance();
    private static volatile Connection connection;

    @SneakyThrows
    public static CacheItemWrapper<Integer> getServerPort() {
        if (TestConfiguration.SERVER_CONNECTION_CACHE_ENABLED) {
            CacheItemWrapper<Integer> port = SERVER_CONNECTION_CACHE.borrowObject();
            log.debug("Returning serverPort {}", port);
            return port;
        } else {
            return new CacheItemWrapper<>(TestConfiguration.SERVER_PORT);
        }
    }

    public static void releaseServerPort(CacheItemWrapper<Integer> port) {
        if (TestConfiguration.SERVER_CONNECTION_CACHE_ENABLED) {
            try {
                SERVER_CONNECTION_CACHE.returnObject(port);
            } catch (Exception e) {
                log.error("Failed releasing serverPort {}", port, e);
            }
        }
    }

    public static void shutdownCaches() {
        SERVER_CONNECTION_CACHE.close();
        DATABASE_CONNECTION_CACHE.close();
    }

    @SneakyThrows
    public static AutoCloseableImpl<Connection> getDatabaseConnection() {
        if (TestConfiguration.DATABASE_CONNECTION_CACHE_ENABLED) {
            BiWrapper<Connection, Integer> biWrapper = DATABASE_CONNECTION_CACHE.borrowObject();
            return new AutoCloseableImpl<>(
                biWrapper.getEntity1(),
                () -> DATABASE_CONNECTION_CACHE.returnObject(biWrapper)
            );
        } else {
            return getLocalConnection();
        }
    }

    @SneakyThrows
    private synchronized static AutoCloseableImpl<Connection> getLocalConnection() {
        if (isNull(connection)) {
            connection = DatabasePortConnectionFactory.getConnection(TestConfiguration.DATABASE_PORT);
        }
        return new AutoCloseableImpl<>(
            connection,
            () -> {
            }
        );
    }
}
