package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.ws;

import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.service.skyxplore.game.common.ws.SkyXploreWsClient;
import com.github.saphyra.apphub.service.skyxplore.game.common.ws.ApphubWsClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PooledSkyXploreDataWsClientFactory implements PooledObjectFactory<SkyXploreWsClient> {
    private static final String SERVICE_NAME_SKYXPLORE_DATA = "skyxplore-data";

    private final ApphubWsClientFactory apphubWsClientFactory;

    @Override
    public PooledObject<SkyXploreWsClient> makeObject() throws Exception {
        log.info("Opening Ws connection...");
        SkyXploreWsClient object = apphubWsClientFactory.create(SERVICE_NAME_SKYXPLORE_DATA, Endpoints.WS_CONNECTION_SKYXPLORE_INTERNAL);

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
