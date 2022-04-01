package com.github.saphyra.apphub.service.skyxplore.game.process;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.GameSleepService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
public class ProcessContext {
    private final ExecutorServiceBean executorServiceBean;
    private final GameSleepService gameSleepService;
    private final SleepService sleepService;
    private final GameProperties gameProperties;
    private final CitizenToModelConverter citizenToModelConverter;
    private final WsMessageSender messageSender;
    private final CitizenToResponseConverter citizenToResponseConverter;
    private final GameDataProxy gameDataProxy;
}
