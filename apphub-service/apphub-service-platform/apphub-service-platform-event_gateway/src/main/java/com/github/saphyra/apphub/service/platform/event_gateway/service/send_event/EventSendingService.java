package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventSendingService {
    private final ExecutorServiceBean executorServiceBean;
    private final LocaleProvider localeProvider;
    private final SendEventRequestValidator sendEventRequestValidator;
    private final SendEventTaskFactory sendEventTaskFactory;
    private final boolean backgroundEventSendingEnabled;

    @Builder
    public EventSendingService(
        ExecutorServiceBean executorServiceBean,
        LocaleProvider localeProvider,
        SendEventRequestValidator sendEventRequestValidator,
        SendEventTaskFactory sendEventTaskFactory,
        @Value("${eventProcessor.backgroundEventSendingEnabled}") boolean backgroundEventSendingEnabled
    ) {
        this.executorServiceBean = executorServiceBean;
        this.localeProvider = localeProvider;
        this.sendEventRequestValidator = sendEventRequestValidator;
        this.sendEventTaskFactory = sendEventTaskFactory;
        this.backgroundEventSendingEnabled = backgroundEventSendingEnabled;
    }

    public void sendEvent(SendEventRequest<?> sendEventRequest) {
        sendEventRequestValidator.validate(sendEventRequest);
        Runnable task = sendEventTaskFactory.create(sendEventRequest, localeProvider.getLocaleValidated());
        if (backgroundEventSendingEnabled && !sendEventRequest.isBlockingRequest()) {
            executorServiceBean.execute(task);
        } else {
            task.run();
        }
    }
}
