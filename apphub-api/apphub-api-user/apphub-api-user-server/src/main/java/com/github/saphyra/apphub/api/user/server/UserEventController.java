package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.event.PageVisitedEvent;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface UserEventController {
    @RequestMapping(method = RequestMethod.POST, path = Endpoints.EVENT_DELETE_ACCOUNT)
    void deleteAccountEvent(@RequestBody SendEventRequest<DeleteAccountEvent> request);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.EVENT_PAGE_VISITED)
    void pageVisitedEvent(@RequestBody SendEventRequest<PageVisitedEvent> request);
}
