package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
public class GameItem {
    private UUID id;
    private UUID gameId;
    private GameItemType type;
}
