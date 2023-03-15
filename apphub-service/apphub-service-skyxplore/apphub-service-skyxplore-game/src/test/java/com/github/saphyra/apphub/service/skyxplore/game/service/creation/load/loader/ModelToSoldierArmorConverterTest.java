package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_armor_piece.BodyPart;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_armor_piece.SoldierArmorPiece;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ModelToSoldierArmorConverterTest {
    private static final UUID ENTITY_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer MAX_DURABILITY = 1324;
    private static final Integer CURRENT_DURABILITY = 56;

    @InjectMocks
    private ModelToSoldierArmorConverter underTest;

    @Test
    public void convert() {
        DurabilityItemModel model = new DurabilityItemModel();
        model.setId(ENTITY_ID);
        model.setDataId(DATA_ID);
        model.setMaxDurability(MAX_DURABILITY);
        model.setCurrentDurability(CURRENT_DURABILITY);

        Map<String, DurabilityItemModel> items = CollectionUtils.singleValueMap(BodyPart.HEAD.getMetadata(), model);

        Map<BodyPart, SoldierArmorPiece> result = underTest.convert(items);

        assertThat(result).hasSize(1);
        SoldierArmorPiece armorPiece = result.get(BodyPart.HEAD);
        assertThat(armorPiece.getSoldierArmorPieceId()).isEqualTo(ENTITY_ID);
        assertThat(armorPiece.getDataId()).isEqualTo(DATA_ID);
        assertThat(armorPiece.getMaxDurability()).isEqualTo(MAX_DURABILITY);
        assertThat(armorPiece.getCurrentDurability()).isEqualTo(CURRENT_DURABILITY);
    }
}