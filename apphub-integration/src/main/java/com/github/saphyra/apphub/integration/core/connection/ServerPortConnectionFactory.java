package com.github.saphyra.apphub.integration.core.connection;

import com.github.saphyra.apphub.integration.core.util.CacheItemWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.net.ServerSocket;

@Slf4j
public class ServerPortConnectionFactory implements PooledObjectFactory<CacheItemWrapper<Integer>> {
    public static GenericObjectPool<CacheItemWrapper<Integer>> getInstance() {
        GenericObjectPoolConfig<CacheItemWrapper<Integer>> config = new GenericObjectPoolConfig<>();
        config.setTestOnBorrow(true);
        config.setMaxWaitMillis(10000);
        config.setMaxTotal(1000);

        return new GenericObjectPool<>(new ServerPortConnectionFactory(), config);
    }

    @Override
    public PooledObject<CacheItemWrapper<Integer>> makeObject() throws Exception {
        Integer port;

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            port = serverSocket.getLocalPort();
        }

        PortForwardTask.portForwardService(port);

        log.debug("ServerPort {} forwarded.", port);

        return new DefaultPooledObject<>(new CacheItemWrapper<>(port));
    }

    @Override
    public void destroyObject(PooledObject<CacheItemWrapper<Integer>> pooledObject) {
        ProcessKiller.killByPort(pooledObject.getObject().getItem());
        log.info("ServerPort {} terminated.", pooledObject.getObject());
    }

    @Override
    public boolean validateObject(PooledObject<CacheItemWrapper<Integer>> pooledObject) {
        Integer port = pooledObject.getObject()
            .getItem();
        log.debug("Validating port {}", port);

        Exception exception = null;

        for (int i = 0; i < 5; i++) {
            try {
                Response response = RequestFactory.createRequest()
                    .get(UrlFactory.create(port, Endpoints.INDEX_PAGE));

                log.debug("Response status of port {}: {}", port, response.getStatusCode());

                return response.getStatusCode() == 200;
            } catch (Exception e) {
                log.debug("ServerPort {} is invalid", port, e);
                exception = e;
            }

            SleepUtil.sleep(1000);
        }

        log.error("ServerPort {} is invalid", port, exception);
        return false;
    }

    @Override
    public void activateObject(PooledObject<CacheItemWrapper<Integer>> pooledObject) {

    }

    @Override
    public void passivateObject(PooledObject<CacheItemWrapper<Integer>> pooledObject) {

    }
}
