package com.github.saphyra.apphub.api.skyxplore.response.game.planet;

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

    public boolean valuePresent() {
        return reservedStorageAmount > 0
            || actualAmount > 0
            || allocatedResourceAmount > 0;
    }
}
