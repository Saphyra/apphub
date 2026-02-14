package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemDomainId;
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
class EquipmentWriteBufferTest {
    private static final String ITEM_NAME = "item-name";
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private EquipmentConverter equipmentConverter;

    @Mock
    private ErrorReporterService errorReporterService;

    @InjectMocks
    private EquipmentWriteBuffer underTest;

    @Mock
    private Equipment domain;

    @Mock
    private EquipmentEntity entity;

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
        given(equipmentConverter.convertDomain(List.of(domain))).willReturn(List.of(entity));

        underTest.doSynchronize(List.of(domain));

        then(equipmentRepository).should().saveAll(List.of(entity));
    }

    @Test
    void synchronize_individually() {
        given(equipmentConverter.convertDomain(List.of(domain))).willReturn(List.of(entity));
        RuntimeException error = new RuntimeException("error");
        given(equipmentRepository.saveAll(List.of(entity))).willThrow(error);

        underTest.doSynchronize(List.of(domain));

        then(errorReporterService).should().report(anyString(), eq(error));
        then(equipmentRepository).should().save(entity);
    }

    @Test
    void synchronize_individually_error() {
        given(equipmentConverter.convertDomain(List.of(domain))).willReturn(List.of(entity, entity));

        RuntimeException error1 = new RuntimeException("error-1");
        given(equipmentRepository.saveAll(List.of(entity, entity))).willThrow(error1);

        RuntimeException error2 = new RuntimeException("error-2");
        given(equipmentRepository.save(entity))
            .willThrow(error2)
            .willAnswer(invocationOnMock -> null);

        underTest.doSynchronize(List.of(domain));

        then(errorReporterService).should().report(anyString(), eq(error1));
        then(errorReporterService).should().report(anyString(), eq(error2));
    }
}