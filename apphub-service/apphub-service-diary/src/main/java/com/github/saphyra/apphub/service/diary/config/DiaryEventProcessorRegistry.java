package com.github.saphyra.apphub.service.diary.config;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiaryEventProcessorRegistry implements EventProcessorRegistry {
    private final String serviceName;

    public DiaryEventProcessorRegistry(
        @Value("${spring.application.name}") String serviceName
    ) {
        this.serviceName = serviceName;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return List.of(
            RegisterProcessorRequest.builder()
                .serviceName(serviceName)
                .eventName(DeleteAccountEvent.EVENT_NAME)
                .url(Endpoints.EVENT_DELETE_ACCOUNT)
                .build()
        );
    }
}
