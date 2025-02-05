package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.settlement;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SettlementDaoTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String SETTLEMENT_NAME = "settlement-name";
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private SettlementConverter converter;

    @Mock
    private SettlementRepository repository;

    @InjectMocks
    private SettlementDao underTest;

    @Mock
    private Settlement domain;

    @Mock
    private SettlementEntity entity;

    @Test
    void findByStarSystemIdAndSettlementName() {
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(repository.findByStarSystemIdAndSettlementName(STAR_SYSTEM_ID_STRING, SETTLEMENT_NAME)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByStarSystemIdAndSettlementName(STAR_SYSTEM_ID, SETTLEMENT_NAME)).contains(domain);
    }
}