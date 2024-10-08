package com.github.saphyra.apphub.api.platform.event_gateway.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface EventGatewayApiServer {
    /**
     * Subscribing the caller for a specific event
     */
    @RequestMapping(method = RequestMethod.PUT, path = GenericEndpoints.REGISTER_PROCESSOR)
    void registerProcessor(@RequestBody RegisterProcessorRequest registerProcessorRequest);

    /**
     * Caller refreshes the subscriptions
     */
    @RequestMapping(method = RequestMethod.GET, path = GenericEndpoints.HEARTBEAT)
    void heartbeat(@PathVariable("serviceName") String serviceName);

    @RequestMapping(method = RequestMethod.POST, path = GenericEndpoints.SEND_EVENT)
    void sendEvent(@RequestBody SendEventRequest<?> sendEventRequest);
}
