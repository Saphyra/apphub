package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.ws;

import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.GenericSkyXploreEndpoints;
import com.github.saphyra.apphub.service.skyxplore.game.common.ws.ApphubWsClientFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.ws.SkyXploreWsClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PooledSkyXploreDataWsClientFactory implements PooledObjectFactory<SkyXploreWsClient> {

    private final ApphubWsClientFactory apphubWsClientFactory;
    private final String skyXploreDataHost;

    public PooledSkyXploreDataWsClientFactory(
        ApphubWsClientFactory apphubWsClientFactory,
        @Value("${skyXploreDataHost}") String skyXploreDataHost
    ) {
        this.apphubWsClientFactory = apphubWsClientFactory;
        this.skyXploreDataHost = skyXploreDataHost;
    }

    @Override
    public PooledObject<SkyXploreWsClient> makeObject() throws Exception {
        log.info("Opening Ws connection...");
        SkyXploreWsClient object = apphubWsClientFactory.create(skyXploreDataHost, GenericSkyXploreEndpoints.WS_CONNECTION_SKYXPLORE_INTERNAL);

        return new DefaultPooledObject<>(object);
    }

    @Override
    public void destroyObject(PooledObject<SkyXploreWsClient> p) throws Exception {
        log.info("Closing Ws Connection...");
        p.getObject()
            .closeBlocking();
    }

    @Override
    public boolean validateObject(PooledObject<SkyXploreWsClient> p) {
        return p.getObject()
            .isOpen();
    }

    @Override
    public void activateObject(PooledObject<SkyXploreWsClient> p) {

    }

    @Override
    public void passivateObject(PooledObject<SkyXploreWsClient> p) {

    }
}
