package com.github.saphyra.apphub.service.platform.event_gateway.service.register;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterProcessorService {
    private final EventProcessorDao eventProcessorDao;
    private final EventProcessorFactory eventProcessorFactory;
    private final DateTimeUtil dateTimeUtil;
    private final RegisterProcessorRequestValidator registerProcessorRequestValidator;

    public void registerProcessor(RegisterProcessorRequest request) {
        registerProcessorRequestValidator.validate(request);
        EventProcessor eventProcessor = eventProcessorDao.findByServiceNameAndEventName(request.getServiceName(), request.getEventName())
            .orElseGet(() -> eventProcessorFactory.create(request));

        eventProcessor.setUrl(request.getUrl());
        eventProcessor.setLastAccess(dateTimeUtil.getCurrentDate());
        eventProcessorDao.save(eventProcessor);
    }
}
