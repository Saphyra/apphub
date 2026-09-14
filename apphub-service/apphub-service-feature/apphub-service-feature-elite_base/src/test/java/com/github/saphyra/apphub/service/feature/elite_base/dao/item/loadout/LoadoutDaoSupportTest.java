package com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout;

import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.equipment.Equipment;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.equipment.EquipmentDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.equipment.EquipmentFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.spaceship.Spaceship;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.spaceship.SpaceshipDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.spaceship.SpaceshipFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LoadoutDaoSupportTest {
    private static final Long MARKET_ID = 32L;
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String NAME = "name";
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();

    @Mock
    private EquipmentDao equipmentDao;

    @Mock
    private SpaceshipDao spaceshipDao;

    @Mock
    private EquipmentFactory equipmentFactory;

    @Mock
    private SpaceshipFactory spaceshipFactory;

    @InjectMocks
    private LoadoutDaoSupport underTest;

    @Mock
    private Equipment equipment;

    @Mock
    private Spaceship spaceship;

    @Test
    void getByMarketId() {
        given(equipmentDao.getByMarketId(MARKET_ID)).willAnswer(_ -> List.of(equipment));

        assertThat(underTest.getByMarketId(ObjectType.EQUIPMENT, MARKET_ID)).containsExactly(equipment);
    }

    @Test
    void getByMarketId_unsupportedType() {
        assertThat(catchThrowable(() -> underTest.getByMarketId(ObjectType.COMMODITY, MARKET_ID))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_equipment() {
        given(equipmentFactory.create(ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME, STAR_SYSTEM_ID)).willReturn(equipment);

        assertThat(underTest.create(ObjectType.EQUIPMENT, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME, STAR_SYSTEM_ID)).isEqualTo(equipment);
    }

    @Test
    void create_spaceship() {
        given(spaceshipFactory.create(ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME, STAR_SYSTEM_ID)).willReturn(spaceship);

        assertThat(underTest.create(ObjectType.SPACESHIP, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME, STAR_SYSTEM_ID)).isEqualTo(spaceship);
    }

    @Test
    void create_unsupported() {
        assertThat(catchThrowable(() -> underTest.create(ObjectType.COMMODITY, null, null, null, null, STAR_SYSTEM_ID))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteAll() {
        List<Loadout> loadouts = List.of(equipment);

        underTest.deleteAll(ObjectType.EQUIPMENT, loadouts);

        then(equipmentDao).should().deleteAllLoadout(loadouts);
    }

    @Test
    void deleteAll_unsupported() {
        List<Loadout> loadouts = List.of(equipment);

        assertThat(catchThrowable(() -> underTest.deleteAll(ObjectType.COMMODITY, loadouts))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void saveAll() {
        List<Loadout> loadouts = List.of(spaceship);

        underTest.saveAll(ObjectType.SPACESHIP, loadouts);

        then(spaceshipDao).should().saveAllLoadout(loadouts);
    }

    @Test
    void saveAll_unsupported() {
        List<Loadout> loadouts = List.of(spaceship);

        assertThat(catchThrowable(() -> underTest.saveAll(ObjectType.COMMODITY, loadouts))).isInstanceOf(IllegalArgumentException.class);
    }
}