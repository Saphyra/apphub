package com.github.saphyra.apphub.integration.backend.model.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueResponse {
    private UUID itemId;
    private String type;
    private Integer requiredWorkPoints;
    private Integer currentWorkPoints;
    private Integer ownPriority;
    private Integer totalPriority;
    private Map<String, Object> data;
}
