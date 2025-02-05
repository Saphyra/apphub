package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction.ConflictingMinorFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction.ConflictingMinorFactionDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction.ConflictingMinorFactionSyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MinorFactionConflictConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String ID_STRING = "id";
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ConflictingMinorFactionDao conflictingMinorFactionDao;

    @Mock
    private ConflictingMinorFactionSyncService conflictingMinorFactionSyncService;

    @InjectMocks
    private MinorFactionConflictConverter underTest;

    @Mock
    private ConflictingMinorFaction conflictingMinorFaction;

    @Test
    void convertDomain() {
        MinorFactionConflict domain = MinorFactionConflict.builder()
            .id(ID)
            .starSystemId(STAR_SYSTEM_ID)
            .status(WarStatus.ACTIVE)
            .warType(WarType.CIVIL_WAR)
            .conflictingMinorFactions(List.of(conflictingMinorFaction))
            .build();

        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(ID_STRING, MinorFactionConflictEntity::getId)
            .returns(STAR_SYSTEM_ID_STRING, MinorFactionConflictEntity::getStarSystemId)
            .returns(WarStatus.ACTIVE, MinorFactionConflictEntity::getStatus)
            .returns(WarType.CIVIL_WAR, MinorFactionConflictEntity::getWarType);

        then(conflictingMinorFactionSyncService).should().sync(ID, List.of(conflictingMinorFaction));
    }

    @Test
    void convertEntity() {
        MinorFactionConflictEntity entity = MinorFactionConflictEntity.builder()
            .id(ID_STRING)
            .starSystemId(STAR_SYSTEM_ID_STRING)
            .status(WarStatus.ACTIVE)
            .warType(WarType.CIVIL_WAR)
            .build();

        given(uuidConverter.convertEntity(ID_STRING)).willReturn(ID);
        given(uuidConverter.convertEntity(STAR_SYSTEM_ID_STRING)).willReturn(STAR_SYSTEM_ID);
        given(conflictingMinorFactionDao.getByConflictId(ID)).willReturn(List.of(conflictingMinorFaction));

        assertThat(underTest.convertEntity(entity))
            .returns(ID, MinorFactionConflict::getId)
            .returns(STAR_SYSTEM_ID, MinorFactionConflict::getStarSystemId)
            .returns(WarStatus.ACTIVE, MinorFactionConflict::getStatus)
            .returns(WarType.CIVIL_WAR, MinorFactionConflict::getWarType)
            .returns(List.of(conflictingMinorFaction), MinorFactionConflict::getConflictingMinorFactions);
    }
}