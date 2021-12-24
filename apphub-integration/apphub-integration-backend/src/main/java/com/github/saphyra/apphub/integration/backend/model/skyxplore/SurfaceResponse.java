package com.github.saphyra.apphub.integration.backend.model.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SurfaceResponse {
    private UUID surfaceId;
    private Coordinate coordinate;
    private String surfaceType;
    private SurfaceBuildingResponse building;
}
