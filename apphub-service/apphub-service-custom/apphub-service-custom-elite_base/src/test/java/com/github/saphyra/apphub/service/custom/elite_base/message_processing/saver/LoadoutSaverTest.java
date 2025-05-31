package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.Loadout;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LoadoutSaverTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Long MARKET_ID = 32423L;
    private static final String EXISTING_ITEM = "existing-item";
    private static final String NEW_ITEM = "new-item";
    private static final String DEPRECATED_ITEM = "deprecated-item";

    @Mock
    private LastUpdateDao lastUpdateDao;

    @Mock
    private LastUpdateFactory lastUpdateFactory;

    @Mock
    private LoadoutDao loadoutDao;

    @Mock
    private LoadoutFactory loadoutFactory;

    @InjectMocks
    private LoadoutSaver underTest;

    @Mock
    private LastUpdate lastUpdate;

    @Mock
    private Loadout existingLoadout;

    @Mock
    private Loadout newLoadout;

    @Mock
    private Loadout deprecatedLoadout;

    @Test
    void nullMarketIdAndCommodityLocation() {
        assertThat(catchThrowable(() -> underTest.save(LAST_UPDATE, LoadoutType.OUTFITTING, null, EXTERNAL_REFERENCE, null, List.of()))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullMarketIdAndExternalReference() {
        assertThat(catchThrowable(() -> underTest.save(LAST_UPDATE, LoadoutType.OUTFITTING, CommodityLocation.STATION, null, null, List.of()))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void save() {
        given(lastUpdateFactory.create(EXTERNAL_REFERENCE, LoadoutType.OUTFITTING, LAST_UPDATE)).willReturn(lastUpdate);
        given(loadoutDao.getByExternalReferenceOrMarketIdAndLoadoutType(EXTERNAL_REFERENCE, MARKET_ID, LoadoutType.OUTFITTING)).willReturn(List.of(existingLoadout, deprecatedLoadout));

        given(existingLoadout.getName()).willReturn(EXISTING_ITEM);
        given(deprecatedLoadout.getName()).willReturn(DEPRECATED_ITEM);

        given(loadoutFactory.create(LAST_UPDATE, LoadoutType.OUTFITTING, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, NEW_ITEM)).willReturn(newLoadout);

        underTest.save(LAST_UPDATE, LoadoutType.OUTFITTING, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, List.of(EXISTING_ITEM, NEW_ITEM));

        then(lastUpdateDao).should().save(lastUpdate);

        then(loadoutDao).should().deleteAll(List.of(deprecatedLoadout));
        then(loadoutDao).should().saveAll(List.of(newLoadout));
    }
}