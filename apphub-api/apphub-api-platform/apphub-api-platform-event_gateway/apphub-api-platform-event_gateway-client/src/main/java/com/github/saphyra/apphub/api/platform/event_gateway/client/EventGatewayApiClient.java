package com.github.saphyra.apphub.api.platform.event_gateway.client;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "event-gateway", url = "${serviceUrls.eventGateway}")
public interface EventGatewayApiClient {
    @RequestMapping(method = RequestMethod.PUT, path = GenericEndpoints.REGISTER_PROCESSOR)
    void registerProcessor(@RequestBody RegisterProcessorRequest registerProcessorRequest);

    @RequestMapping(method = RequestMethod.GET, path = GenericEndpoints.HEARTBEAT)
    void heartbeat(@PathVariable("serviceName") String serviceName);

    @RequestMapping(method = RequestMethod.POST, path = GenericEndpoints.SEND_EVENT)
    void sendEvent(@RequestBody SendEventRequest<?> sendEventRequest, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
