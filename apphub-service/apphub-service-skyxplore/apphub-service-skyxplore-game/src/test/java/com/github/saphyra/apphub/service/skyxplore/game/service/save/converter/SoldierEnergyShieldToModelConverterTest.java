package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.SoldierEnergyShield;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.SoldierEnergyShield.CITIZEN_ENERGY_SHIELD;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SoldierEnergyShieldToModelConverterTest {
    private static final UUID ENTITY_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int MAX_DURABILITY = 45;
    private static final int CURRENT_DURABILITY = 56;
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @InjectMocks
    private SoldierEnergyShieldToModelConverter underTest;

    @Test
    public void convert() {
        SoldierEnergyShield energyShield = SoldierEnergyShield.builder()
            .entityId(ENTITY_ID)
            .dataId(DATA_ID)
            .maxDurability(MAX_DURABILITY)
            .currentDurability(CURRENT_DURABILITY)
            .build();

        DurabilityItemModel result = underTest.convert(CITIZEN_ID, GAME_ID, energyShield);

        assertThat(result.getId()).isEqualTo(ENTITY_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.DURABILITY_ITEM_MODEL);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getMaxDurability()).isEqualTo(MAX_DURABILITY);
        assertThat(result.getCurrentDurability()).isEqualTo(CURRENT_DURABILITY);
        assertThat(result.getParent()).isEqualTo(CITIZEN_ID);
        assertThat(result.getMetadata()).isEqualTo(CITIZEN_ENERGY_SHIELD);
    }
}