package com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override;

import com.github.saphyra.apphub.api.custom.elite_base.model.MaterialType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MaterialTraderOverrideConverterTest {
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final String STATION_ID_STRING = "station-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private MaterialTraderOverrideConverter underTest;

    @Test
    void convertDomain() {
        MaterialTraderOverride domain = MaterialTraderOverride.builder()
            .stationId(STATION_ID)
            .materialType(MaterialType.RAW)
            .build();

        given(uuidConverter.convertDomain(STATION_ID)).willReturn(STATION_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(STATION_ID_STRING, MaterialTraderOverrideEntity::getStationId)
            .returns(MaterialType.RAW, MaterialTraderOverrideEntity::getMaterialType);
    }

    @Test
    void convertEntity() {
        MaterialTraderOverrideEntity entity = MaterialTraderOverrideEntity.builder()
            .stationId(STATION_ID_STRING)
            .materialType(MaterialType.RAW)
            .build();

        given(uuidConverter.convertEntity(STATION_ID_STRING)).willReturn(STATION_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(STATION_ID, MaterialTraderOverride::getStationId)
            .returns(MaterialType.RAW, MaterialTraderOverride::getMaterialType);
    }
}