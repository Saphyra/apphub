package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.ws;

import com.github.saphyra.apphub.service.skyxplore.game.common.ws.SkyXploreWsClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Component;

@Component
class WebSocketClientCacheConfig extends GenericObjectPoolConfig<SkyXploreWsClient> {
    WebSocketClientCacheConfig() {
        setMaxTotal(10);
    }
}
