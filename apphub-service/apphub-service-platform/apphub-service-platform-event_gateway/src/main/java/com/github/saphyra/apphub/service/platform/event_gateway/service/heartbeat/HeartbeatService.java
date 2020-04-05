package com.github.saphyra.apphub.service.platform.event_gateway.service.heartbeat;

import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import com.github.saphyra.util.OffsetDateTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeartbeatService {
    private final EventProcessorDao eventProcessorDao;
    private final OffsetDateTimeProvider offsetDateTimeProvider;

    public void heartbeat(String serviceName) {
        List<EventProcessor> eventProcessors = eventProcessorDao.getByServiceName(serviceName);
        OffsetDateTime currentDate = offsetDateTimeProvider.getCurrentDate();
        eventProcessors.forEach(processor -> processor.setLastAccess(currentDate));

        eventProcessorDao.saveAll(eventProcessors);
    }
}
