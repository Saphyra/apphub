package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.minor_faction.state;

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
class MinorFactionStateDaoTest {
    private static final UUID MINOR_FACTION_ID = UUID.randomUUID();
    private static final String MINOR_FACTION_ID_STRING = "minor-faction-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private MinorFactionStateConverter converter;

    @Mock
    private MinorFactionStateRepository repository;

    @InjectMocks
    private MinorFactionStateDao underTest;

    @Mock
    private MinorFactionState domain;

    @Mock
    private MinorFactionStateEntity entity;

    @Test
    void getByMinorFactionIdAndStatus() {
        given(uuidConverter.convertDomain(MINOR_FACTION_ID)).willReturn(MINOR_FACTION_ID_STRING);
        given(repository.getByMinorFactionIdAndStatus(MINOR_FACTION_ID_STRING, StateStatus.ACTIVE)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByMinorFactionIdAndStatus(MINOR_FACTION_ID, StateStatus.ACTIVE)).containsExactly(domain);
    }

    @Test
    void getByMinorFactionId() {
        given(uuidConverter.convertDomain(MINOR_FACTION_ID)).willReturn(MINOR_FACTION_ID_STRING);
        given(repository.getByMinorFactionId(MINOR_FACTION_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByMinorFactionId(MINOR_FACTION_ID)).containsExactly(domain);
    }
}