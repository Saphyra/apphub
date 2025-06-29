package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict.PowerplayConflict;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict.PowerplayConflictFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.message.PowerplayConflictProgress;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PowerplayConflictFactoryTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final Double CONFLICT_PROGRESS = 34d;

    @InjectMocks
    private PowerplayConflictFactory underTest;

    @Test
    void create() {
        PowerplayConflictProgress powerplayConflictProgress = PowerplayConflictProgress.builder()
            .power(Power.NAKATO_KAINE.getValue())
            .conflictProgress(CONFLICT_PROGRESS)
            .build();

        CustomAssertions.singleListAssertThat(underTest.create(STAR_SYSTEM_ID, List.of(powerplayConflictProgress)))
            .returns(STAR_SYSTEM_ID, PowerplayConflict::getStarSystemId)
            .returns(Power.NAKATO_KAINE, PowerplayConflict::getPower)
            .returns(CONFLICT_PROGRESS, PowerplayConflict::getConflictProgress);
    }
}