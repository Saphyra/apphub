package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.last_update;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LastUpdateDaoTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private LastUpdateConverter lastUpdateConverter;

    @Mock
    private LastUpdateRepository lastUpdateRepository;

    @Mock
    private LastUpdateCache cache;

    @InjectMocks
    private LastUpdateDao underTest;

    @Mock
    private LastUpdate domain;

    @Mock
    private LastUpdateEntity entity;

    @Mock
    private LastUpdate storedDomain;

    @Test
    void extractId() {
        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(domain.getType()).willReturn(EntityType.COMMODITY);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);

        assertThat(underTest.extractId(domain))
            .returns(EXTERNAL_REFERENCE_STRING, LastUpdateId::getExternalReference)
            .returns(EntityType.COMMODITY, LastUpdateId::getType);
    }

    @Test
    void shouldSave_newEntity(){
        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(domain.getType()).willReturn(EntityType.COMMODITY);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);

        LastUpdateId id = LastUpdateId.builder()
            .externalReference(EXTERNAL_REFERENCE_STRING)
            .type(EntityType.COMMODITY)
            .build();
        given(cache.getIfPresent(id)).willReturn(Optional.empty());
        given(lastUpdateRepository.findById(id)).willReturn(Optional.empty());

        assertThat(underTest.shouldSave(domain)).isTrue();
    }

    @Test
    void shouldSave_matchingLastUpdate(){
        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(domain.getType()).willReturn(EntityType.COMMODITY);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);

        LastUpdateId id = LastUpdateId.builder()
            .externalReference(EXTERNAL_REFERENCE_STRING)
            .type(EntityType.COMMODITY)
            .build();
        given(cache.getIfPresent(id)).willReturn(Optional.of(storedDomain));
        given(domain.getLastUpdate()).willReturn(LAST_UPDATE);
        given(storedDomain.getLastUpdate()).willReturn(LAST_UPDATE);

        assertThat(underTest.shouldSave(domain)).isFalse();
    }

    @Test
    void shouldSave_differentLastUpdate(){
        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(domain.getType()).willReturn(EntityType.COMMODITY);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);

        LastUpdateId id = LastUpdateId.builder()
            .externalReference(EXTERNAL_REFERENCE_STRING)
            .type(EntityType.COMMODITY)
            .build();
        given(cache.getIfPresent(id)).willReturn(Optional.of(storedDomain));
        given(domain.getLastUpdate()).willReturn(LAST_UPDATE);
        given(storedDomain.getLastUpdate()).willReturn(LAST_UPDATE.minusSeconds(1));

        assertThat(underTest.shouldSave(domain)).isTrue();
    }
}