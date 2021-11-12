package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability_item;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class DurabilityItemConverter extends ConverterBase<DurabilityItemEntity, DurabilityItemModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected DurabilityItemModel processEntityConversion(DurabilityItemEntity entity) {
        DurabilityItemModel model = new DurabilityItemModel();
        model.setId(uuidConverter.convertEntity(entity.getDurabilityItemId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.DURABILITY_ITEM_MODEL);
        model.setParent(uuidConverter.convertEntity(entity.getParent()));
        model.setMetadata(entity.getMetadata());
        model.setMaxDurability(entity.getMaxDurability());
        model.setCurrentDurability(entity.getCurrentDurability());
        model.setDataId(entity.getDataId());
        return model;
    }

    @Override
    protected DurabilityItemEntity processDomainConversion(DurabilityItemModel domain) {
        return DurabilityItemEntity.builder()
            .durabilityItemId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .parent(uuidConverter.convertDomain(domain.getParent()))
            .metadata(domain.getMetadata())
            .maxDurability(domain.getMaxDurability())
            .currentDurability(domain.getCurrentDurability())
            .dataId(domain.getDataId())
            .build();
    }
}
