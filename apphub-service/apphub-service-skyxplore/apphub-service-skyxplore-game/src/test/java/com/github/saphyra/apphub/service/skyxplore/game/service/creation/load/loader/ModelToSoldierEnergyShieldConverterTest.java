package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.SoldierEnergyShield;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ModelToSoldierEnergyShieldConverterTest {
    private static final UUID ENTITY_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer MAX_DURABILITY = 657;
    private static final Integer CURRENT_DURABILITY = 87564;

    @InjectMocks
    private ModelToSoldierEnergyShieldConverter underTest;

    @Test
    public void convert() {
        DurabilityItemModel model = new DurabilityItemModel();
        model.setId(ENTITY_ID);
        model.setDataId(DATA_ID);
        model.setMaxDurability(MAX_DURABILITY);
        model.setCurrentDurability(CURRENT_DURABILITY);

        SoldierEnergyShield result = underTest.convert(model);

        assertThat(result.getEntityId()).isEqualTo(ENTITY_ID);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getMaxDurability()).isEqualTo(MAX_DURABILITY);
        assertThat(result.getCurrentDurability()).isEqualTo(CURRENT_DURABILITY);
    }
}