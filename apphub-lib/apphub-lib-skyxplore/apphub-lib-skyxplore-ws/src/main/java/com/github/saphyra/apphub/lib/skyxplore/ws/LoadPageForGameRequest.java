package com.github.saphyra.apphub.lib.skyxplore.ws;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoadPageForGameRequest {
    private UUID gameId;
    private GameItemType type;
    private Integer page;
}
