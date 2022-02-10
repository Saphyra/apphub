package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import com.github.saphyra.apphub.service.platform.event_gateway.service.local_event.LocalEventProcessor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Builder
class SendEventTaskFactory {
    private final EventProcessorDao eventProcessorDao;
    private final EventSender eventSender;
    private final ExecutorServiceBean executorServiceBean;
    private final List<LocalEventProcessor> localEventProcessors;
    private final ErrorReporterService errorReporterService;

    SendEventTask create(SendEventRequest<?> sendEventRequest, String locale) {
        return SendEventTask.builder()
            .eventProcessorDao(eventProcessorDao)
            .eventSender(eventSender)
            .sendEventRequest(sendEventRequest)
            .locale(locale)
            .executorServiceBean(executorServiceBean)
            .localEventProcessors(localEventProcessors)
            .errorReporterService(errorReporterService)
            .build();
    }
}
