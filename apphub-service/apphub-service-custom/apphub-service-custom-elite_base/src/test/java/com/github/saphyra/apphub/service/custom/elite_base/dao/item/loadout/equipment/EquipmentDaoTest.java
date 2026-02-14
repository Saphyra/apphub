package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemDomainId;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import com.google.common.cache.Cache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EquipmentDaoTest {
    private static final long MARKET_ID = 314L;
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final String ITEM_NAME = "item-name";
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private EquipmentConverter converter;

    @Mock
    private EquipmentRepository repository;

    @Mock
    private Cache<Long, List<Equipment>> readCache;

    @Mock
    private EquipmentWriteBuffer writeBuffer;

    @Mock
    private EquipmentDeleteBuffer deleteBuffer;

    @InjectMocks
    private EquipmentDao underTest;

    @Mock
    private Equipment domain;

    @Mock
    private EquipmentEntity entity;

    @Test
    void getCacheKey() {
        given(domain.getMarketId()).willReturn(MARKET_ID);

        assertThat(underTest.getCacheKey(domain)).isEqualTo(MARKET_ID);
    }

    @Test
    void toDomainId() {
        ItemEntityId itemEntityId = ItemEntityId.builder()
            .externalReference(EXTERNAL_REFERENCE_STRING)
            .itemName(ITEM_NAME)
            .build();

        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);

        assertThat(underTest.toDomainId(itemEntityId))
            .returns(EXTERNAL_REFERENCE, ItemDomainId::getExternalReference)
            .returns(ITEM_NAME, ItemDomainId::getItemName);
    }

    @Test
    void getDomainId() {
        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(domain.getItemName()).willReturn(ITEM_NAME);

        assertThat(underTest.getDomainId(domain))
            .returns(EXTERNAL_REFERENCE, ItemDomainId::getExternalReference)
            .returns(ITEM_NAME, ItemDomainId::getItemName);
    }

    @Test
    void matchesWithId_matches() {
        ItemDomainId domainId = ItemDomainId.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .itemName(ITEM_NAME)
            .build();

        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(domain.getItemName()).willReturn(ITEM_NAME);

        assertThat(underTest.matchesWithId(domainId, domain)).isTrue();
    }

    @Test
    void matchesWithId_differentExternalReference() {
        ItemDomainId domainId = ItemDomainId.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .itemName(ITEM_NAME)
            .build();

        given(domain.getExternalReference()).willReturn(UUID.randomUUID());

        assertThat(underTest.matchesWithId(domainId, domain)).isFalse();
    }

    @Test
    void matchesWithId_differentItemName() {
        ItemDomainId domainId = ItemDomainId.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .itemName(ITEM_NAME)
            .build();

        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(domain.getItemName()).willReturn("different-item-name");

        assertThat(underTest.matchesWithId(domainId, domain)).isFalse();
    }

    @Test
    void getByMarketId_cached() {
        given(readCache.getIfPresent(MARKET_ID)).willReturn(List.of(domain));

        //noinspection unchecked
        List<Equipment> result = (List<Equipment>) underTest.getByMarketId(MARKET_ID);

        assertThat(result).containsExactly(domain);

        then(repository).shouldHaveNoInteractions();
    }

    @Test
    void getByMarketId_notCached() {
        given(readCache.getIfPresent(MARKET_ID)).willReturn(null);
        given(repository.getByMarketId(MARKET_ID)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        //noinspection unchecked
        List<Equipment> result = (List<Equipment>) underTest.getByMarketId(MARKET_ID);

        assertThat(result).containsExactly(domain);
    }

    @Test
    void deleteAllLoadout() {
        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(domain.getItemName()).willReturn(ITEM_NAME);
        given(readCache.asMap()).willReturn(new ConcurrentHashMap<>());

        ItemDomainId domainId = ItemDomainId.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .itemName(ITEM_NAME)
            .build();

        underTest.deleteAllLoadout(List.of(domain));

        then(deleteBuffer).should().add(domainId);
    }

    @Test
    void saveAllLoadout() {
        given(domain.getMarketId()).willReturn(MARKET_ID);

        underTest.saveAllLoadout(List.of(domain));

        then(readCache).should().invalidate(MARKET_ID);
    }
}