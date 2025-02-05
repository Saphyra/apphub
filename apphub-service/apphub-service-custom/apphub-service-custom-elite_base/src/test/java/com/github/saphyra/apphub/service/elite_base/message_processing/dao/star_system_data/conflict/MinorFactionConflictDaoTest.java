package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict;

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
class MinorFactionConflictDaoTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";
    private static final UUID ID = UUID.randomUUID();
    private static final String ID_STRING = "id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private MinorFactionConflictConverter converter;

    @Mock
    private MinorFactionConflictRepository repository;

    @InjectMocks
    private MinorFactionConflictDao underTest;

    @Mock
    private MinorFactionConflict domain;

    @Mock
    private MinorFactionConflictEntity entity;

    @Test
    void getByStarSystemId() {
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(repository.getByStarSystemId(STAR_SYSTEM_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByStarSystemId(STAR_SYSTEM_ID)).containsExactly(domain);
    }

    @Test
    void deleteById() {
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(repository.existsById(ID_STRING)).willReturn(true);

        underTest.deleteById(ID);

        then(repository).should().deleteById(ID_STRING);
    }
}