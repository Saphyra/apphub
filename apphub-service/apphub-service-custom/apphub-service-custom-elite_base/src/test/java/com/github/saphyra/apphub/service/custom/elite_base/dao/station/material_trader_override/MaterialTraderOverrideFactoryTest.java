package com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override;

import com.github.saphyra.apphub.api.custom.elite_base.model.material_trader.CreateMaterialTraderOverrideRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.MaterialType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MaterialTraderOverrideFactoryTest {
    private static final UUID STATION_ID = UUID.randomUUID();

    @InjectMocks
    private MaterialTraderOverrideFactory underTest;

    @Test
    void create() {
        CreateMaterialTraderOverrideRequest request = CreateMaterialTraderOverrideRequest.builder()
            .stationId(STATION_ID)
            .materialType(MaterialType.RAW)
            .build();

        assertThat(underTest.create(request, true))
            .returns(STATION_ID, MaterialTraderOverride::getStationId)
            .returns(MaterialType.RAW, MaterialTraderOverride::getMaterialType)
            .returns(true, MaterialTraderOverride::isVerified);
    }
}