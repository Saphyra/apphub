package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LoadoutDeleteBufferTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String LOADOUT_NAME = "loadout-name";
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";

    @Mock
    private LoadoutRepository repository;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private LoadoutDeleteBuffer underTest;

    @Captor
    private ArgumentCaptor<List<LoadoutEntityId>> argumentCaptor;

    @Test
    void doSynchronize() {
        LoadoutDomainId domainId = LoadoutDomainId.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .type(LoadoutType.OUTFITTING)
            .name(LOADOUT_NAME)
            .build();
        underTest.add(domainId);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);

        underTest.synchronize();

        then(repository).should().deleteAllById(argumentCaptor.capture());

        CustomAssertions.singleListAssertThat(argumentCaptor.getValue())
            .returns(EXTERNAL_REFERENCE_STRING, LoadoutEntityId::getExternalReference)
            .returns(LoadoutType.OUTFITTING, LoadoutEntityId::getType)
            .returns(LOADOUT_NAME, LoadoutEntityId::getName);
    }
}