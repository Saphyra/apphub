package com.github.saphyra.apphub.service.platform.event_gateway.service.heartbeat;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessHeartbeatService {
    private final EventProcessorDao eventProcessorDao;
    private final DateTimeUtil dateTimeUtil;

    public void heartbeat(String serviceName) {
        List<EventProcessor> eventProcessors = eventProcessorDao.getByServiceName(serviceName);
        LocalDateTime currentDate = dateTimeUtil.getCurrentDate();
        eventProcessors.forEach(processor -> processor.setLastAccess(currentDate));

        eventProcessorDao.saveAll(eventProcessors);
    }
}
