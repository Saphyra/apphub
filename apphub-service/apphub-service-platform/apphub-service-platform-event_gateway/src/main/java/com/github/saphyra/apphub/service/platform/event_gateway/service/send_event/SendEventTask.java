package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    @Override
    public void run() {
        eventProcessorDao.getByEventName(sendEventRequest.getEventName())
            .stream()
            .parallel()
            .forEach(processor -> eventSender.sendEvent(processor, sendEventRequest));
        log.info("Event with name {} is sent to the processors.", sendEventRequest.getEventName());
    }
}
