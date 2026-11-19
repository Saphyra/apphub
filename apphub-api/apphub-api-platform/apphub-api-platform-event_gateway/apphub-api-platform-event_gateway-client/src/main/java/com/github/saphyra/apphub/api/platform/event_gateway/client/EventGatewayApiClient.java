package com.github.saphyra.apphub.api.platform.event_gateway.client;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.common.GenericEndpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

public interface EventGatewayApiClient {
    @PutExchange(GenericEndpoints.REGISTER_PROCESSOR)
    void registerProcessor(@RequestBody RegisterProcessorRequest registerProcessorRequest);

    @GetExchange(GenericEndpoints.HEARTBEAT)
    void heartbeat(@PathVariable("serviceName") String serviceName);

    @PostExchange(GenericEndpoints.SEND_EVENT)
    void sendEvent(@RequestBody SendEventRequest<?> sendEventRequest);
}
