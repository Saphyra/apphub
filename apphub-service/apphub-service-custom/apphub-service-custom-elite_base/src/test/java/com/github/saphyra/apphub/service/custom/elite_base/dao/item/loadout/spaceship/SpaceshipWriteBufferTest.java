package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemDomainId;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SpaceshipWriteBufferTest {
    private static final String ITEM_NAME = "item-name";
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private SpaceshipRepository repository;

    @Mock
    private SpaceshipConverter converter;

    @Mock
    private ErrorReporterService errorReporterService;

    @InjectMocks
    private SpaceshipWriteBuffer underTest;

    @Mock
    private Spaceship domain;

    @Mock
    private SpaceshipEntity entity;

    @Test
    void getDomainId() {
        given(domain.getItemName()).willReturn(ITEM_NAME);
        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);

        assertThat(underTest.getDomainId(domain))
            .returns(ITEM_NAME, ItemDomainId::getItemName)
            .returns(EXTERNAL_REFERENCE, ItemDomainId::getExternalReference);
    }

    @Test
    void synchronize() {
        given(converter.convertDomain(List.of(domain))).willReturn(List.of(entity));

        underTest.doSynchronize(List.of(domain));

        then(repository).should().saveAll(List.of(entity));
    }

    @Test
    void synchronize_individually() {
        given(converter.convertDomain(List.of(domain))).willReturn(List.of(entity));
        RuntimeException error = new RuntimeException("error");
        given(repository.saveAll(List.of(entity))).willThrow(error);

        underTest.doSynchronize(List.of(domain));

        then(errorReporterService).should().report(anyString(), eq(error));
        then(repository).should().save(entity);
    }

    @Test
    void synchronize_individually_error() {
        given(converter.convertDomain(List.of(domain))).willReturn(List.of(entity, entity));

        RuntimeException error1 = new RuntimeException("error-1");
        given(repository.saveAll(List.of(entity, entity))).willThrow(error1);

        RuntimeException error2 = new RuntimeException("error-2");
        given(repository.save(entity))
            .willThrow(error2)
            .willAnswer(invocationOnMock -> null);

        underTest.doSynchronize(List.of(domain));

        then(errorReporterService).should().report(anyString(), eq(error1));
        then(errorReporterService).should().report(anyString(), eq(error2));
    }
}