package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.powerplay_conflict;

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

@ExtendWith(MockitoExtension.class)
class PowerplayConflictDaoTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private PowerplayConflictConverter converter;

    @Mock
    private PowerplayConflictRepository repository;

    @InjectMocks
    private PowerplayConflictDao underTest;

    @Mock
    private PowerplayConflictEntity entity;

    @Mock
    private PowerplayConflict domain;

    @Test
    void getByStarSystemId() {
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(repository.getByIdStarSystemId(STAR_SYSTEM_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByStarSystemId(STAR_SYSTEM_ID)).containsExactly(domain);
    }
}