package com.github.saphyra.apphub.service.feature.elite_base.dao.last_update;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.google.common.cache.Cache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LastUpdateDaoTest {
    private static final String EXTERNAL_REFERENCE = "external-reference";
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();

     @Mock
     private LastUpdateFactory lastUpdateFactory;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private LastUpdateConverter converter;

    @Mock
    private LastUpdateRepository repository;

    @Mock
    private Cache<LastUpdateId, LastUpdate> cache;

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
        given(domain.getType()).willReturn(ObjectType.COMMODITY);

        assertThat(underTest.extractId(domain))
            .returns(EXTERNAL_REFERENCE, LastUpdateId::getExternalReference)
            .returns(ObjectType.COMMODITY, LastUpdateId::getObjectType);
    }

    @Test
    void shouldSave_newEntity() {
        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(domain.getType()).willReturn(ObjectType.COMMODITY);

        LastUpdateId id = LastUpdateId.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .objectType(ObjectType.COMMODITY)
            .build();
        given(cache.getIfPresent(id)).willReturn(null);
        given(repository.findById(id)).willReturn(Optional.empty());

        assertThat(underTest.shouldSave(domain)).isTrue();
    }

    @Test
    void shouldSave_matchingLastUpdate() {
        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(domain.getType()).willReturn(ObjectType.COMMODITY);

        LastUpdateId id = LastUpdateId.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .objectType(ObjectType.COMMODITY)
            .build();
        given(cache.getIfPresent(id)).willReturn(storedDomain);
        given(domain.getLastUpdate()).willReturn(LAST_UPDATE);
        given(storedDomain.getLastUpdate()).willReturn(LAST_UPDATE);

        assertThat(underTest.shouldSave(domain)).isFalse();
    }

    @Test
    void shouldSave_differentLastUpdate() {
        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(domain.getType()).willReturn(ObjectType.COMMODITY);

        LastUpdateId id = LastUpdateId.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .objectType(ObjectType.COMMODITY)
            .build();
        given(cache.getIfPresent(id)).willReturn(storedDomain);
        given(domain.getLastUpdate()).willReturn(LAST_UPDATE);
        given(storedDomain.getLastUpdate()).willReturn(LAST_UPDATE.minusSeconds(1));

        assertThat(underTest.shouldSave(domain)).isTrue();
    }

    @Test
    void findByIdValidated_notFound() {
        LastUpdateId id = LastUpdateId.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .objectType(ObjectType.EQUIPMENT)
            .build();
        given(cache.getIfPresent(id)).willReturn(null);
        given(repository.findById(id)).willReturn(Optional.empty());
        given(lastUpdateFactory.create(EXTERNAL_REFERENCE, ObjectType.EQUIPMENT)).willReturn(domain);

        assertThat(underTest.findByIdOrDefault(EXTERNAL_REFERENCE, ObjectType.EQUIPMENT)).isEqualTo(domain);
    }

    @Test
    void findByIdValidated() {
        LastUpdateId id = LastUpdateId.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .objectType(ObjectType.EQUIPMENT)
            .build();
        given(cache.getIfPresent(id)).willReturn(domain);

        assertThat(underTest.findByIdOrDefault(EXTERNAL_REFERENCE, ObjectType.EQUIPMENT)).isEqualTo(domain);
    }

    @Test
    void findById() {
        LastUpdateId id = LastUpdateId.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .objectType(ObjectType.EQUIPMENT)
            .build();
        given(cache.getIfPresent(id)).willReturn(domain);

        assertThat(underTest.findById(EXTERNAL_REFERENCE, ObjectType.EQUIPMENT)).contains(domain);
    }
}