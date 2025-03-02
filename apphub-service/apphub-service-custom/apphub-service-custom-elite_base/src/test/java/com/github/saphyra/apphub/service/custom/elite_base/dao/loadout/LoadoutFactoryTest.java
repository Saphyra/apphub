package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.Loadout;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LoadoutFactoryTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Long MARKET_ID = 245L;
    private static final String NAME = "name";

    @InjectMocks
    private LoadoutFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(LAST_UPDATE, LoadoutType.OUTFITTING, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME))
            .returns(LAST_UPDATE, Loadout::getLastUpdate)
            .returns(LoadoutType.OUTFITTING, Loadout::getType)
            .returns(CommodityLocation.STATION, Loadout::getCommodityLocation)
            .returns(EXTERNAL_REFERENCE, Loadout::getExternalReference)
            .returns(MARKET_ID, Loadout::getMarketId)
            .returns(NAME, Loadout::getName);
    }
}