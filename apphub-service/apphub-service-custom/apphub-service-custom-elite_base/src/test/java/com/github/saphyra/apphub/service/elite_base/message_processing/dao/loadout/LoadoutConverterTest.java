package com.github.saphyra.apphub.service.elite_base.message_processing.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.EntityType;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdateId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LoadoutConverterTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String NAME = "name";
    private static final Long MARKET_ID = 234L;
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private LastUpdateDao lastUpdateDao;

    @InjectMocks
    private LoadoutConverter underTest;

    @Test
    void convertDomain() {
        Loadout domain = Loadout.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .type(LoadoutType.OUTFITTING)
            .name(NAME)
            .commodityLocation(CommodityLocation.STATION)
            .marketId(MARKET_ID)
            .build();

        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(EXTERNAL_REFERENCE_STRING, LoadoutEntity::getExternalReference)
            .returns(LoadoutType.OUTFITTING, LoadoutEntity::getType)
            .returns(NAME, LoadoutEntity::getName)
            .returns(CommodityLocation.STATION, LoadoutEntity::getCommodityLocation)
            .returns(MARKET_ID, LoadoutEntity::getMarketId);
    }

    @Test
    void convertEntity() {
        LoadoutEntity entity = LoadoutEntity.builder()
            .externalReference(EXTERNAL_REFERENCE_STRING)
            .type(LoadoutType.OUTFITTING)
            .name(NAME)
            .commodityLocation(CommodityLocation.STATION)
            .marketId(MARKET_ID)
            .build();

        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);
        given(lastUpdateDao.findById(LastUpdateId.builder().externalReference(EXTERNAL_REFERENCE_STRING).type(EntityType.SHIP_MODULE).build()))
            .willReturn(Optional.of(LastUpdate.builder().lastUpdate(LAST_UPDATE).build()));

        assertThat(underTest.convertEntity(entity))
            .returns(EXTERNAL_REFERENCE, Loadout::getExternalReference)
            .returns(LoadoutType.OUTFITTING, Loadout::getType)
            .returns(NAME, Loadout::getName)
            .returns(CommodityLocation.STATION, Loadout::getCommodityLocation)
            .returns(MARKET_ID, Loadout::getMarketId)
            .returns(LAST_UPDATE, Loadout::getLastUpdate);
    }
}