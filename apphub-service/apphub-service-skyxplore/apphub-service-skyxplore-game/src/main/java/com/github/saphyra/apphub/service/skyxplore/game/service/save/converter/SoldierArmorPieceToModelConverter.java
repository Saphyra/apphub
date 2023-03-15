package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_armor_piece.BodyPart;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_armor_piece.SoldierArmorPiece;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class SoldierArmorPieceToModelConverter {
    List<DurabilityItemModel> convert(UUID citizenId, UUID gameId, Map<BodyPart, SoldierArmorPiece> armor) {
        return armor.entrySet()
            .stream()
            .map(entry -> convert(citizenId, gameId, entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    private DurabilityItemModel convert(UUID citizenId, UUID gameId, BodyPart bodyPart, SoldierArmorPiece armorPiece) {
        DurabilityItemModel model = new DurabilityItemModel();
        model.setId(armorPiece.getSoldierArmorPieceId());
        model.setGameId(gameId);
        model.setType(GameItemType.DURABILITY_ITEM_MODEL);
        model.setMaxDurability(armorPiece.getMaxDurability());
        model.setCurrentDurability(armorPiece.getCurrentDurability());
        model.setParent(citizenId);
        model.setMetadata(bodyPart.getMetadata());
        model.setDataId(armorPiece.getDataId());
        return model;
    }
}
