package com.github.saphyra.apphub.api.skyxplore.response.game.planet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConstructionResponse {
    private UUID constructionId;
    private Integer requiredWorkPoints;
    private Integer currentWorkPoints;
    private String data;
}
