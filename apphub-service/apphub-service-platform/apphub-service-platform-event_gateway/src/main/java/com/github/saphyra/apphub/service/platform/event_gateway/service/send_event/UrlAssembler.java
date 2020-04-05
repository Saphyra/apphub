package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import org.springframework.stereotype.Component;

@Component
class UrlAssembler {
    String assemble(EventProcessor processor) {
        return processor.getServiceName() + processor.getUrl();
    }
}
