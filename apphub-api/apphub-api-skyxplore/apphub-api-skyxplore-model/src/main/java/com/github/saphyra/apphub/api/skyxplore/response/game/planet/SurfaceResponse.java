package com.github.saphyra.apphub.api.skyxplore.response.game.planet;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
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
    private SurfaceConstructionAreaResponse constructionArea;
    private ConstructionResponse terraformation;
}
