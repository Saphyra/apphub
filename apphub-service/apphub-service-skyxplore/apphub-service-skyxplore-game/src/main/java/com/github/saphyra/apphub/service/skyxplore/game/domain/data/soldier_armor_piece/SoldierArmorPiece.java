package com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_armor_piece;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SoldierArmorPiece {
    private final UUID soldierArmorPieceId;
    private final UUID citizenId;
    private final BodyPart bodyPart;
    private final String dataId;
}
