package com.github.saphyra.apphub.service.feature.elite_base.dao.last_update;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LastUpdateFactoryTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private LastUpdateFactory underTest;

    @Test
    void create() {
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);

        assertThat(underTest.create(EXTERNAL_REFERENCE, ObjectType.COMMODITY, LAST_UPDATE))
            .returns(EXTERNAL_REFERENCE_STRING, LastUpdate::getExternalReference)
            .returns(ObjectType.COMMODITY, LastUpdate::getType)
            .returns(LAST_UPDATE, LastUpdate::getLastUpdate);
    }

    @Test
    void create_defaultTimestamp(){
        given(dateTimeUtil.getZeroLocalDateTime()).willReturn(LAST_UPDATE);

        assertThat(underTest.create(EXTERNAL_REFERENCE_STRING, ObjectType.COMMODITY))
            .returns(EXTERNAL_REFERENCE_STRING, LastUpdate::getExternalReference)
            .returns(ObjectType.COMMODITY, LastUpdate::getType)
            .returns(LAST_UPDATE, LastUpdate::getLastUpdate);
    }
}