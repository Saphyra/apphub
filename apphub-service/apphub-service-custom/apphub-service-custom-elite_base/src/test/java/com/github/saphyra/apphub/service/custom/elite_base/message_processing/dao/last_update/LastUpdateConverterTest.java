package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.last_update;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
class LastUpdateConverterTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final String LAST_UPDATE_STRING = "last-update";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeConverter dateTimeConverter;

    @InjectMocks
    private LastUpdateConverter underTest;

    @Test
    void convertDomain() {
        LastUpdate domain = LastUpdate.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .type(EntityType.COMMODITY)
            .lastUpdate(LAST_UPDATE)
            .build();

        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);
        given(dateTimeConverter.convertDomain(LAST_UPDATE)).willReturn(LAST_UPDATE_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(EXTERNAL_REFERENCE_STRING, lastUpdateEntity -> lastUpdateEntity.getId().getExternalReference())
            .returns(EntityType.COMMODITY, lastUpdateEntity -> lastUpdateEntity.getId().getType())
            .returns(LAST_UPDATE_STRING, LastUpdateEntity::getLastUpdate);
    }

    @Test
    void convertEntity() {
        LastUpdateEntity domain = LastUpdateEntity.builder()
            .id(LastUpdateId.builder()
                .externalReference(EXTERNAL_REFERENCE_STRING)
                .type(EntityType.COMMODITY)
                .build())

            .lastUpdate(LAST_UPDATE_STRING)
            .build();

        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);
        given(dateTimeConverter.convertToLocalDateTime(LAST_UPDATE_STRING)).willReturn(LAST_UPDATE);

        assertThat(underTest.convertEntity(domain))
            .returns(EXTERNAL_REFERENCE, LastUpdate::getExternalReference)
            .returns(EntityType.COMMODITY, LastUpdate::getType)
            .returns(LAST_UPDATE, LastUpdate::getLastUpdate);
    }
}