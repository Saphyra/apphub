package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.google.common.cache.Cache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class LoadoutDaoTest {
    private static final Long MARKET_ID = 3142L;
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final String LOADOUT_NAME = "loadout-name";
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();

    @Mock
    private LoadoutConverter converter;

    @Mock
    private LoadoutRepository repository;

    @Mock
    private Cache<LoadoutCacheKey, List<Loadout>> readCache;

    @Mock
    private LoadoutWriteBuffer writeBuffer;

    @Mock
    private LoadoutDeleteBuffer deleteBuffer;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private LoadoutDao underTest;

    @Mock
    private Loadout domain;

    @Mock
    private LoadoutEntity entity;

    @Test
    void getByMarketIdAndType() {
        given(repository.getByMarketIdAndType(MARKET_ID, LoadoutType.OUTFITTING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByMarketIdAndType(MARKET_ID, LoadoutType.OUTFITTING)).containsExactly(domain);
    }

    @Test
    void getCacheKey() {
        given(domain.getMarketId()).willReturn(MARKET_ID);
        given(domain.getType()).willReturn(LoadoutType.OUTFITTING);

        assertThat(underTest.getCacheKey(domain))
            .returns(MARKET_ID, LoadoutCacheKey::getMarketId)
            .returns(LoadoutType.OUTFITTING, LoadoutCacheKey::getType);
    }

    @Test
    void toDomainId() {
        LoadoutEntityId loadoutEntityId = LoadoutEntityId.builder()
            .externalReference(EXTERNAL_REFERENCE_STRING)
            .type(LoadoutType.OUTFITTING)
            .name(LOADOUT_NAME)
            .build();

        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);

        assertThat(underTest.toDomainId(loadoutEntityId))
            .returns(EXTERNAL_REFERENCE, LoadoutDomainId::getExternalReference)
            .returns(LoadoutType.OUTFITTING, LoadoutDomainId::getType)
            .returns(LOADOUT_NAME, LoadoutDomainId::getName);
    }

    @Test
    void getDomainId() {
        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(domain.getType()).willReturn(LoadoutType.OUTFITTING);
        given(domain.getName()).willReturn(LOADOUT_NAME);

        assertThat(underTest.getDomainId(domain))
            .returns(EXTERNAL_REFERENCE, LoadoutDomainId::getExternalReference)
            .returns(LoadoutType.OUTFITTING, LoadoutDomainId::getType)
            .returns(LOADOUT_NAME, LoadoutDomainId::getName);
    }

    @ParameterizedTest
    @MethodSource("matchesWithIdProvider")
    void matchesWithId(UUID externalReference, LoadoutType loadoutType, String loadoutName, boolean expected) {
        LoadoutDomainId domainId = LoadoutDomainId.builder()
            .externalReference(externalReference)
            .type(loadoutType)
            .name(loadoutName)
            .build();

        lenient().when(domain.getExternalReference()).thenReturn(EXTERNAL_REFERENCE);
        lenient().when(domain.getType()).thenReturn(LoadoutType.OUTFITTING);
        lenient().when(domain.getName()).thenReturn(LOADOUT_NAME);

        assertThat(underTest.matchesWithId(domainId, domain)).isEqualTo(expected);
    }

    static Stream<Arguments> matchesWithIdProvider() {
        return Stream.of(
            Arguments.of(EXTERNAL_REFERENCE, LoadoutType.OUTFITTING, LOADOUT_NAME, true),
            Arguments.of(EXTERNAL_REFERENCE, LoadoutType.OUTFITTING, "other-name", false),
            Arguments.of(EXTERNAL_REFERENCE, LoadoutType.SHIPYARD, LOADOUT_NAME, false),
            Arguments.of(UUID.randomUUID(), LoadoutType.OUTFITTING, LOADOUT_NAME, false)
        );
    }
}