package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SendEventTaskFactory {
    private final EventProcessorDao eventProcessorDao;
    private final EventSender eventSender;

    SendEventTask create(SendEventRequest<?> sendEventRequest, String locale) {
        return SendEventTask.builder()
            .eventProcessorDao(eventProcessorDao)
            .eventSender(eventSender)
            .sendEventRequest(sendEventRequest)
            .locale(locale)
            .build();
    }
}
