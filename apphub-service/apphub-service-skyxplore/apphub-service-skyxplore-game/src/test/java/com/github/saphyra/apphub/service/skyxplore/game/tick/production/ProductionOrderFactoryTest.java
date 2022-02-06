package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ProductionOrderFactoryTest {
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int AMOUNT = 4;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ProductionOrderFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(PRODUCTION_ORDER_ID);

        ProductionOrder result = underTest.create(EXTERNAL_REFERENCE, LOCATION, LocationType.PLANET, DATA_ID, AMOUNT);

        assertThat(result.getProductionOrderId()).isEqualTo(PRODUCTION_ORDER_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getLocationType()).isEqualTo(LocationType.PLANET);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
        assertThat(result.getAssignee()).isNull();
        assertThat(result.getRequiredWorkPoints()).isEqualTo(0);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(0);
    }
}