package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Builder
@Getter(value = AccessLevel.PACKAGE)
public class SendEventTask implements Runnable {
    @NonNull
    private final EventProcessorDao eventProcessorDao;

    @NonNull
    private final EventSender eventSender;

    @NonNull
    private final SendEventRequest<?> sendEventRequest;

    @NonNull
    private final String locale;

    @NonNull
    private final ExecutorServiceBean executorServiceBean;

    @Override
    public void run() {
        List<EventProcessor> eventProcessors = eventProcessorDao.getByEventName(sendEventRequest.getEventName());
        executorServiceBean.processCollectionWithWait(eventProcessors, eventProcessor -> {
            eventSender.sendEvent(eventProcessor, sendEventRequest, locale);
            return null;
        });

        log.info("Event with name {} is sent to the processors.", sendEventRequest.getEventName());
    }
}
