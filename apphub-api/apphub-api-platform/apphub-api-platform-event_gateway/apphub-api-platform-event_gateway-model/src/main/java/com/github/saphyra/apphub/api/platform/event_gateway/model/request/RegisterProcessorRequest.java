package com.github.saphyra.apphub.api.platform.event_gateway.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RegisterProcessorRequest {
    private String serviceName;
    private String url;
    private String eventName;
}
