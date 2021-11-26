package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.BodyPart;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.SoldierArmorPiece;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SoldierArmorPieceToModelConverterTest {
    private static final UUID ENTITY_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int MAX_DURABILITY = 6527;
    private static final int CURRENT_DURABILITY = 654;
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @InjectMocks
    private SoldierArmorPieceToModelConverter underTest;

    @Test
    public void convert() {
        SoldierArmorPiece armorPiece = SoldierArmorPiece.builder()
            .entityId(ENTITY_ID)
            .dataId(DATA_ID)
            .maxDurability(MAX_DURABILITY)
            .currentDurability(CURRENT_DURABILITY)
            .build();

        List<DurabilityItemModel> result = underTest.convert(CITIZEN_ID, GAME_ID, CollectionUtils.singleValueMap(BodyPart.HEAD, armorPiece));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(ENTITY_ID);
        assertThat(result.get(0).getGameId()).isEqualTo(GAME_ID);
        assertThat(result.get(0).getType()).isEqualTo(GameItemType.DURABILITY_ITEM_MODEL);
        assertThat(result.get(0).getMaxDurability()).isEqualTo(MAX_DURABILITY);
        assertThat(result.get(0).getCurrentDurability()).isEqualTo(CURRENT_DURABILITY);
        assertThat(result.get(0).getParent()).isEqualTo(CITIZEN_ID);
        assertThat(result.get(0).getMetadata()).isEqualTo(BodyPart.HEAD.getMetadata());
        assertThat(result.get(0).getDataId()).isEqualTo(DATA_ID);
    }
}