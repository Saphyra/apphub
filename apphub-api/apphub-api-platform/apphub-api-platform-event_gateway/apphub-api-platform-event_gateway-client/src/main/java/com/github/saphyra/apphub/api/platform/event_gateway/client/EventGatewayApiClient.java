package com.github.saphyra.apphub.api.platform.event_gateway.client;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.endpoint.Endpoint;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("event-gateway")
public interface EventGatewayApiClient {
    @RequestMapping(method = RequestMethod.PUT, path = Endpoint.REGISTER_PROCESSOR)
    void registerProcessor(@RequestBody RegisterProcessorRequest registerProcessorRequest);

    @RequestMapping(method = RequestMethod.GET, path = Endpoint.HEARTBEAT)
    void heartbeat(@PathVariable("serviceName") String serviceName);

    @RequestMapping(method = RequestMethod.POST, path = Endpoint.SEND_EVENT)
    void sendEvent(@RequestBody SendEventRequest sendEventRequest);
}
