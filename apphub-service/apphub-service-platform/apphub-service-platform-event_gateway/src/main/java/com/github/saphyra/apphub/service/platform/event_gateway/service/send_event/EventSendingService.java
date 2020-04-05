package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import com.github.saphyra.util.OffsetDateTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventSendingService {
    private final ExecutorServiceBean executorServiceBean;
    private final EventProcessorDao eventProcessorDao;
    private final OffsetDateTimeProvider offsetDateTimeProvider;
    private final RestTemplate restTemplate;
    private final UrlAssembler urlAssembler;

    public void sendEvent(SendEventRequest<?> sendEventRequest) {
        executorServiceBean.execute(() -> {
                eventProcessorDao.getByEventName(sendEventRequest.getEventName())
                    .stream()
                    .parallel()
                    .forEach(processor -> sendEvent(processor, sendEventRequest));
                log.info("Event with name {} is sent to the processors.", sendEventRequest.getEventName());
            }
        );
    }

    private void sendEvent(EventProcessor processor, SendEventRequest<?> sendEventRequest) {
        try {
            String url = urlAssembler.assemble(processor);
            log.info("Url: {}", url);
            restTemplate.postForEntity(url, sendEventRequest, Void.class);
            processor.setLastAccess(offsetDateTimeProvider.getCurrentDate());
            eventProcessorDao.save(processor);
        } catch (Exception e) {
            log.warn("Failed sending event with name {} to processor {}", sendEventRequest.getEventName(), processor, e);
        }
    }
}
