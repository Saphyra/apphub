package com.github.saphyra.apphub.service.skyxplore.game.common.ws;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApphubWsClientFactory {
    private final ObjectMapper objectMapper;
    private final SleepService sleepService;

    public SkyXploreWsClient create(String service, String endpoint) throws Exception {
        String url = String.format("ws://%s%s", service, endpoint);
        log.info("ApphubWsClient URL: {}", url);
        return SkyXploreWsClient.builder()
            .url(url)
            .objectMapper(objectMapper)
            .sleepService(sleepService)
            .build();
    }
}
