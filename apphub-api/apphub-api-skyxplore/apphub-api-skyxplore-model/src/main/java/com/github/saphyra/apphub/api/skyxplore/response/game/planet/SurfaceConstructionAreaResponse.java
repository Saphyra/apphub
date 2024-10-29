package com.github.saphyra.apphub.api.skyxplore.response.game.planet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurfaceConstructionAreaResponse {
    private UUID constructionAreaId;
    private String dataId;
    private ConstructionResponse construction;
    private DeconstructionResponse deconstruction;
}
