package com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MaterialTraderOverrideDaoTest {
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final String STATION_ID_STRING = "station-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private MaterialTraderOverrideConverter converter;

    @Mock
    private MaterialTraderOverrideRepository repository;

    @InjectMocks
    private MaterialTraderOverrideDao underTest;

    @Mock
    private MaterialTraderOverrideEntity entity;

    @Mock
    private MaterialTraderOverride domain;

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(STATION_ID)).willReturn(STATION_ID_STRING);
        given(repository.findById(STATION_ID_STRING)).willReturn(Optional.empty());

        ExceptionValidator.validateNotLoggedException(() -> underTest.findByIdValidated(STATION_ID), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(STATION_ID)).willReturn(STATION_ID_STRING);
        given(repository.findById(STATION_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByIdValidated(STATION_ID)).isEqualTo(domain);
    }
}