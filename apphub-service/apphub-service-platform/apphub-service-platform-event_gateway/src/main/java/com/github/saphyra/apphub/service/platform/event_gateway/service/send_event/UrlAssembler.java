package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import org.springframework.stereotype.Component;

@Component
class UrlAssembler {
    private static final String URI_PREFIX = "http://";

    String assemble(EventProcessor processor) {
        return URI_PREFIX + processor.getServiceName() + processor.getUrl();
    }
}
