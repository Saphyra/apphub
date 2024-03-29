package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ResourceDetailsResponse {
    private String dataId;
    private int reservedStorageAmount;
    private int actualAmount;
    private int allocatedResourceAmount;
}
