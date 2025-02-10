package com.github.saphyra.apphub.integration.structure.api.elite_base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class NearestMaterialTraderResponse {
    private UUID starId;
    private String starName;
    private UUID stationId;
    private String stationName;
    private Double distanceFromReference;
    private Double distanceFromStar;
    private MaterialType materialType;
}
