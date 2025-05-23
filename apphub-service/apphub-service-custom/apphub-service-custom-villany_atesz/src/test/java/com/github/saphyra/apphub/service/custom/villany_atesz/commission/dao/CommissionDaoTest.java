package com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommissionDaoTest {
    private static final UUID COMMISSION_ID = UUID.randomUUID();
    private static final String COMMISSION_ID_STRING = "commission-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CommissionConverter converter;

    @Mock
    private CommissionRepository repository;

    @InjectMocks
    private CommissionDao underTest;

    @Mock
    private Commission domain;

    @Mock
    private CommissionEntity entity;

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(COMMISSION_ID)).willReturn(COMMISSION_ID_STRING);
        given(repository.findById(COMMISSION_ID_STRING)).willReturn(Optional.empty());
        given(converter.convertEntity(Optional.empty())).willReturn(Optional.empty());

        ExceptionValidator.validateNotLoggedException(() -> underTest.findByIdValidated(COMMISSION_ID), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(COMMISSION_ID)).willReturn(COMMISSION_ID_STRING);
        given(repository.findById(COMMISSION_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByIdValidated(COMMISSION_ID)).isEqualTo(domain);
    }

    @Test
    void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByUserId(USER_ID)).containsExactly(domain);
    }

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }
}