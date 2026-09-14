package com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.Loadout;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.LoadoutDaoSupport;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.type.ItemTypeDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LoadoutSaverTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final String ITEM_NAME_1 = "item-NAME-1";
    private static final Long MARKET_ID = 2345L;
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();

    @Mock
    private LastUpdateDao lastUpdateDao;

    @Mock
    private LastUpdateFactory lastUpdateFactory;

    @Mock
    private LoadoutDaoSupport loadoutDaoSupport;

    @Mock
    private ItemTypeDao itemTypeDao;

    @InjectMocks
    private LoadoutSaver underTest;

    @Mock
    private LastUpdate lastUpdate;

    @Mock
    private Loadout newLoadout;

    @Mock
    private Loadout existingLoadout;

    @Test
    void nullMarketId() {
        assertThat(catchThrowable(() -> underTest.save(TIMESTAMP, ObjectType.EQUIPMENT, ItemLocationType.STATION, EXTERNAL_REFERENCE, null, List.of(ITEM_NAME_1), STAR_SYSTEM_ID)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void outdatedMessage() {
        given(lastUpdateDao.findById(EXTERNAL_REFERENCE, ObjectType.EQUIPMENT)).willReturn(Optional.of(lastUpdate));
        given(lastUpdate.getLastUpdate()).willReturn(TIMESTAMP.plusSeconds(1));

        underTest.save(TIMESTAMP, ObjectType.EQUIPMENT, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, List.of(ITEM_NAME_1), STAR_SYSTEM_ID);

        then(loadoutDaoSupport).shouldHaveNoInteractions();
    }


    @Test
    void saveNew() {
        given(lastUpdateDao.findById(EXTERNAL_REFERENCE, ObjectType.EQUIPMENT)).willReturn(Optional.empty());
        given(lastUpdateFactory.create(EXTERNAL_REFERENCE, ObjectType.EQUIPMENT, TIMESTAMP)).willReturn(lastUpdate);
        given(loadoutDaoSupport.getByMarketId(ObjectType.EQUIPMENT, MARKET_ID)).willReturn(List.of());
        given(loadoutDaoSupport.create(ObjectType.EQUIPMENT, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, ITEM_NAME_1.toLowerCase(), STAR_SYSTEM_ID)).willReturn(newLoadout);

        underTest.save(TIMESTAMP, ObjectType.EQUIPMENT, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, List.of(ITEM_NAME_1), STAR_SYSTEM_ID);

        then(lastUpdateDao).should().save(lastUpdate);
        then(itemTypeDao).should().saveAll(ObjectType.EQUIPMENT, List.of(ITEM_NAME_1.toLowerCase()));
        then(loadoutDaoSupport).should().deleteAll(ObjectType.EQUIPMENT, List.of());
        then(loadoutDaoSupport).should().saveAll(ObjectType.EQUIPMENT, List.of(newLoadout));
    }

    @Test
    void shouldNotSaveExisting() {
        given(lastUpdateDao.findById(EXTERNAL_REFERENCE, ObjectType.EQUIPMENT)).willReturn(Optional.empty());
        given(lastUpdateFactory.create(EXTERNAL_REFERENCE, ObjectType.EQUIPMENT, TIMESTAMP)).willReturn(lastUpdate);
        given(loadoutDaoSupport.getByMarketId(ObjectType.EQUIPMENT, MARKET_ID)).willReturn(List.of(existingLoadout));
        given(existingLoadout.getItemName()).willReturn(ITEM_NAME_1.toLowerCase());

        underTest.save(TIMESTAMP, ObjectType.EQUIPMENT, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, List.of(ITEM_NAME_1), STAR_SYSTEM_ID);

        then(lastUpdateDao).should().save(lastUpdate);
        then(itemTypeDao).should().saveAll(ObjectType.EQUIPMENT, List.of(ITEM_NAME_1.toLowerCase()));
        then(loadoutDaoSupport).should().deleteAll(ObjectType.EQUIPMENT, List.of());
        then(loadoutDaoSupport).should().saveAll(ObjectType.EQUIPMENT, List.of());
    }

    @Test
    void delete() {
        given(lastUpdateDao.findById(EXTERNAL_REFERENCE, ObjectType.EQUIPMENT)).willReturn(Optional.empty());
        given(lastUpdateFactory.create(EXTERNAL_REFERENCE, ObjectType.EQUIPMENT, TIMESTAMP)).willReturn(lastUpdate);
        given(loadoutDaoSupport.getByMarketId(ObjectType.EQUIPMENT, MARKET_ID)).willReturn(List.of(existingLoadout));
        given(existingLoadout.getItemName()).willReturn(ITEM_NAME_1.toLowerCase());

        underTest.save(TIMESTAMP, ObjectType.EQUIPMENT, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, List.of(), STAR_SYSTEM_ID);

        then(lastUpdateDao).should().save(lastUpdate);
        then(itemTypeDao).should().saveAll(ObjectType.EQUIPMENT, List.of());
        then(loadoutDaoSupport).should().deleteAll(ObjectType.EQUIPMENT, List.of(existingLoadout));
        then(loadoutDaoSupport).should().saveAll(ObjectType.EQUIPMENT, List.of());
    }
}