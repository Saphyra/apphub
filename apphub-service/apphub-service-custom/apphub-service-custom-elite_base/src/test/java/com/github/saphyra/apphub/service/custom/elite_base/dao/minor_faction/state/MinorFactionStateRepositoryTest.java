package com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state;

import com.github.saphyra.apphub.service.custom.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.MinorFactionStateEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.MinorFactionStateRepository;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.StateStatus;
import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class MinorFactionStateRepositoryTest {
    private static final String MINOR_FACTION_ID_1 = "minor-faction-id-1";
    private static final String MINOR_FACTION_ID_2 = "minor-faction-id-2";

    @Autowired
    private MinorFactionStateRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByMinorFactionIdAndStatus() {
        MinorFactionStateEntity entity1 = MinorFactionStateEntity.builder()
            .minorFactionId(MINOR_FACTION_ID_1)
            .status(StateStatus.ACTIVE)
            .state(FactionStateEnum.BLIGHT)
            .build();
        underTest.save(entity1);
        MinorFactionStateEntity entity2 = MinorFactionStateEntity.builder()
            .minorFactionId(MINOR_FACTION_ID_2)
            .status(StateStatus.ACTIVE)
            .state(FactionStateEnum.BLIGHT)
            .build();
        underTest.save(entity2);
        MinorFactionStateEntity entity3 = MinorFactionStateEntity.builder()
            .minorFactionId(MINOR_FACTION_ID_1)
            .status(StateStatus.PENDING)
            .state(FactionStateEnum.BLIGHT)
            .build();
        underTest.save(entity3);

        assertThat(underTest.getByMinorFactionIdAndStatus(MINOR_FACTION_ID_1, StateStatus.ACTIVE)).containsExactly(entity1);
    }

    @Test
    void getByMinorFactionId() {
        MinorFactionStateEntity entity1 = MinorFactionStateEntity.builder()
            .minorFactionId(MINOR_FACTION_ID_1)
            .status(StateStatus.ACTIVE)
            .state(FactionStateEnum.BLIGHT)
            .build();
        underTest.save(entity1);
        MinorFactionStateEntity entity2 = MinorFactionStateEntity.builder()
            .minorFactionId(MINOR_FACTION_ID_2)
            .status(StateStatus.ACTIVE)
            .state(FactionStateEnum.BLIGHT)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByMinorFactionId(MINOR_FACTION_ID_1)).containsExactly(entity1);
    }
}