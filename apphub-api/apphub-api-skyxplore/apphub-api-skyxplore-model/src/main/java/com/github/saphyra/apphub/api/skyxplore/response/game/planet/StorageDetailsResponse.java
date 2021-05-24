package com.github.saphyra.apphub.api.skyxplore.response.game.planet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StorageDetailsResponse {
    private int capacity;
    private int reservedStorageAmount;
    private int actualResourceAmount;
    private int allocatedResourceAmount;
    private List<ResourceDetailsResponse> resourceDetails;
}
