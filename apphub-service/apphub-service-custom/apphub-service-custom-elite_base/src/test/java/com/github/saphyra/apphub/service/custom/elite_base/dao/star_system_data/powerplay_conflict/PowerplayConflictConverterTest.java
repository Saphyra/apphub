package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.powerplay_conflict;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.Power;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PowerplayConflictConverterTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final Double CONFLICT_PROGRESS = 24d;
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private PowerplayConflictConverter underTest;

    @Test
    void convertDomain() {
        PowerplayConflict domain = PowerplayConflict.builder()
            .starSystemId(STAR_SYSTEM_ID)
            .power(Power.NAKATO_KAINE)
            .conflictProgress(CONFLICT_PROGRESS)
            .build();

        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(STAR_SYSTEM_ID_STRING, entity -> entity.getId().getStarSystemId())
            .returns(Power.NAKATO_KAINE, entity -> entity.getId().getPower())
            .returns(CONFLICT_PROGRESS, PowerplayConflictEntity::getConflictProgress);
    }

    @Test
    void convertEntity() {
        PowerplayConflictEntity entity = PowerplayConflictEntity.builder()
            .id(PowerplayConflictEntityId.builder()
                .starSystemId(STAR_SYSTEM_ID_STRING)
                .power(Power.NAKATO_KAINE)
                .build())
            .conflictProgress(CONFLICT_PROGRESS)
            .build();

        given(uuidConverter.convertEntity(STAR_SYSTEM_ID_STRING)).willReturn(STAR_SYSTEM_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(STAR_SYSTEM_ID, PowerplayConflict::getStarSystemId)
            .returns(Power.NAKATO_KAINE, PowerplayConflict::getPower)
            .returns(CONFLICT_PROGRESS, PowerplayConflict::getConflictProgress);
    }
}