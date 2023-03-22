package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.ws;

import com.github.saphyra.apphub.service.skyxplore.game.common.ws.SkyXploreWsClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.stereotype.Component;

@Component
public class WebSocketClientCache extends GenericObjectPool<SkyXploreWsClient> {
    public WebSocketClientCache(PooledSkyXploreDataWsClientFactory factory, WebSocketClientCacheConfig config) {
        super(factory, config);
    }
}
