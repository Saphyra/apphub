package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Getter
class TickSchedulerContext {
    private final List<TickTask> tickTasks;
    private final SleepService sleepService;
    private final DateTimeUtil dateTimeUtil;
    private final GameProperties gameProperties;
}
