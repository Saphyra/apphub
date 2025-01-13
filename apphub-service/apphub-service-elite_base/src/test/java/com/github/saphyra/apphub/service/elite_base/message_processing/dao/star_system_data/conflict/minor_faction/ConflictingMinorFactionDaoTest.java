package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
class ConflictingMinorFactionDaoTest {
    private static final UUID CONFLICT_ID = UUID.randomUUID();
    private static final String CONFLICT_ID_STRING = "conflict-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ConflictingMinorFactionConverter converter;

    @Mock
    private ConflictingMinorFactionRepository repository;

    @InjectMocks
    private ConflictingMinorFactionDao underTest;

    @Mock
    private ConflictingMinorFaction domain;

    @Mock
    private ConflictingMinorFactionEntity entity;

    @Test
    void getByConflictId() {
        given(uuidConverter.convertDomain(CONFLICT_ID)).willReturn(CONFLICT_ID_STRING);
        given(repository.getByConflictId(CONFLICT_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByConflictId(CONFLICT_ID)).containsExactly(domain);
    }

    @Test
    void deleteByConflictId() {
        given(uuidConverter.convertDomain(CONFLICT_ID)).willReturn(CONFLICT_ID_STRING);

        underTest.deleteByConflictId(CONFLICT_ID);

        then(repository).should().deleteByConflictId(CONFLICT_ID_STRING);
    }
}