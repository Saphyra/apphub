package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.elite_base.common.BufferSynchronizationService;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemPosition;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemDataDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AcquisitionTargetProviderTest {
    private static final UUID STRONGHOLD_STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID FORTIFIED_STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID UNION_UNOCCUPIED_STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID STRONGHOLD_UNOCCUPIED_STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID OCCUPIED_STAR_SYSTEM_ID = UUID.randomUUID();
    private static final StarSystemPosition STRONGHOLD_COORDINATE = new StarSystemPosition(0d, 0d, 0d);
    private static final StarSystemPosition FORTIFIED_COORDINATE = new StarSystemPosition(40d, 0d, 0d);
    private static final StarSystemPosition UNION_UNOCCUPIED_COORDINATE = new StarSystemPosition(25d, 0d, 0d);
    private static final StarSystemPosition STRONGHOLD_UNOCCUPIED_COORDINATE = new StarSystemPosition(5d, 0d, 0d);
    private static final StarSystemPosition OCCUPIED_COORDINATE = new StarSystemPosition(10d, 0d, 0d);

    @Autowired
    private AcquisitionTargetProvider underTest;

    @Autowired
    private StarSystemDao starSystemDao;

    @Autowired
    private StarSystemDataDao starSystemDataDao;

    @Autowired
    private BufferSynchronizationService bufferSynchronizationService;

    @Test
    void getAcquisitionTargets() {
        BiWrapper<StarSystem, StarSystemData> strongholdSystem = createStarSystem(STRONGHOLD_STAR_SYSTEM_ID, STRONGHOLD_COORDINATE, PowerplayState.STRONGHOLD);
        BiWrapper<StarSystem, StarSystemData> fortifiedSystem = createStarSystem(FORTIFIED_STAR_SYSTEM_ID, FORTIFIED_COORDINATE, PowerplayState.FORTIFIED);
        BiWrapper<StarSystem, StarSystemData> unionUnoccupiedSystem = createStarSystem(UNION_UNOCCUPIED_STAR_SYSTEM_ID, UNION_UNOCCUPIED_COORDINATE, PowerplayState.UNOCCUPIED);
        BiWrapper<StarSystem, StarSystemData> strongholdUnoccupiedSystem = createStarSystem(STRONGHOLD_UNOCCUPIED_STAR_SYSTEM_ID, STRONGHOLD_UNOCCUPIED_COORDINATE, PowerplayState.UNOCCUPIED);
        createStarSystem(OCCUPIED_STAR_SYSTEM_ID, OCCUPIED_COORDINATE, PowerplayState.EXPLOITED);

        bufferSynchronizationService.synchronizeAll();

        Map<UUID, List<StarSystem>> result = underTest.getAcquisitionTargets(
            List.of(strongholdSystem.getEntity1(), fortifiedSystem.getEntity1()),
            List.of(strongholdSystem.getEntity2(), fortifiedSystem.getEntity2()));
        assertThat(result).containsKeys(STRONGHOLD_STAR_SYSTEM_ID, FORTIFIED_STAR_SYSTEM_ID);
        assertThat(result.get(STRONGHOLD_STAR_SYSTEM_ID)).containsExactlyInAnyOrder(unionUnoccupiedSystem.getEntity1(), strongholdUnoccupiedSystem.getEntity1());
        assertThat(result.get(FORTIFIED_STAR_SYSTEM_ID)).containsExactlyInAnyOrder(unionUnoccupiedSystem.getEntity1());
    }

    private BiWrapper<StarSystem, StarSystemData> createStarSystem(UUID starSystemId, StarSystemPosition position, PowerplayState state) {
        StarSystem starSystem = StarSystem.builder()
            .id(starSystemId)
            .starName(starSystemId.toString())
            .position(position)
            .build();
        StarSystemData data = StarSystemData.builder()
            .starSystemId(starSystemId)
            .powerplayState(state)
            .build();

        starSystemDao.save(starSystem);
        starSystemDataDao.save(data);

        return new BiWrapper<>(starSystem, data);
    }
}