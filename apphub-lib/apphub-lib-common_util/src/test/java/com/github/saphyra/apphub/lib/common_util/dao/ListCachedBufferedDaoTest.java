package com.github.saphyra.apphub.lib.common_util.dao;

import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import com.google.common.cache.Cache;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ListCachedBufferedDaoTest {
    private static final UUID DOMAIN_ID_1 = UUID.randomUUID();
    private static final UUID DOMAIN_ID_2 = UUID.randomUUID();
    private static final String ENTITY_ID = "entity-id";

    @Mock
    private Converter<Entity, Domain> converter;

    @Mock
    private CrudRepository<Entity, String> repository;

    @Mock
    private Cache<ListCacheKey, List<Domain>> readCache;

    @Mock
    private WriteBuffer<UUID, Domain> writeBuffer;

    @Mock
    private DeleteBuffer<UUID> deleteBuffer;

    @Mock
    private Function<Domain, ListCacheKey> listCacheKeyExtractor;

    @Mock
    private Function<String, UUID> idConverter;

    @Mock
    private Function<Domain, UUID> domainIdExtractor;

    @Mock
    private BiFunction<UUID, Domain, Boolean> idMatcher;

    private ListCachedBufferedDaoImpl underTest;

    @Mock
    private Entity entity;

    @Mock
    private Domain domain1;

    @Mock
    private Domain domain2;

    @Mock
    private ListCacheKey listCacheKey;

    @Mock
    private Function<Domain, Boolean> search;

    @Mock
    private Supplier<Optional<Entity>> query;

    @Mock
    private BiFunction<Domain, Domain, Domain> mergeFunction;

    @BeforeEach
    void setUp() {
        underTest = ListCachedBufferedDaoImpl.builder()
            .converter(converter)
            .repository(repository)
            .readCache(readCache)
            .writeBuffer(writeBuffer)
            .deleteBuffer(deleteBuffer)
            .listCacheKeyExtractor(listCacheKeyExtractor)
            .idConverter(idConverter)
            .domainIdExtractor(domainIdExtractor)
            .idMatcher(idMatcher)
            .build();
    }

    @Test
    void delete() {
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        ConcurrentMap<ListCacheKey, List<Domain>> map = new ConcurrentHashMap<>();
        map.put(listCacheKey, List.of(domain1));
        given(readCache.asMap()).willReturn(map);
        given(idMatcher.apply(DOMAIN_ID_1, domain1)).willReturn(true);

        underTest.delete(domain1);

        then(readCache).should().invalidate(listCacheKey);
        then(writeBuffer).should().remove(DOMAIN_ID_1);
        then(deleteBuffer).should().add(DOMAIN_ID_1);
    }

    @Test
    void deleteAll() {
        underTest.deleteAll();

        then(readCache).should().invalidateAll();
        then(writeBuffer).should().removeAll();
        then(deleteBuffer).should().removeAll();
        then(repository).should().deleteAll();
    }

    @Test
    void deleteAllDomains() {
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        ConcurrentMap<ListCacheKey, List<Domain>> map = new ConcurrentHashMap<>();
        map.put(listCacheKey, List.of(domain1));
        given(readCache.asMap()).willReturn(map);
        given(idMatcher.apply(DOMAIN_ID_1, domain1)).willReturn(true);

        underTest.deleteAll(List.of(domain1));

        then(readCache).should().invalidate(listCacheKey);
        then(writeBuffer).should().remove(DOMAIN_ID_1);
        then(deleteBuffer).should().add(DOMAIN_ID_1);
    }

    @Test
    void deleteById() {
        given(idConverter.apply(ENTITY_ID)).willReturn(DOMAIN_ID_1);
        ConcurrentMap<ListCacheKey, List<Domain>> map = new ConcurrentHashMap<>();
        map.put(listCacheKey, List.of(domain1));
        given(readCache.asMap()).willReturn(map);
        given(idMatcher.apply(DOMAIN_ID_1, domain1)).willReturn(true);

        underTest.deleteById(ENTITY_ID);

        then(readCache).should().invalidate(listCacheKey);
        then(writeBuffer).should().remove(DOMAIN_ID_1);
        then(deleteBuffer).should().add(DOMAIN_ID_1);
    }

    @Test
    void findAll_deleted() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(true);

        assertThat(underTest.findAll()).isEmpty();
    }

    @Test
    void findAll() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(false);
        given(writeBuffer.getIfPresent(DOMAIN_ID_1)).willReturn(Optional.empty());

        assertThat(underTest.findAll()).containsExactly(domain1);
    }

    @Test
    void findAll_inWriteBuffer() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(false);
        given(writeBuffer.getIfPresent(DOMAIN_ID_1)).willReturn(Optional.of(domain2));

        assertThat(underTest.findAll()).containsExactly(domain2);
    }

    @Test
    void findAllById_deleted() {
        given(repository.findAllById(List.of(ENTITY_ID))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(true);

        assertThat(underTest.findAllById(List.of(ENTITY_ID))).isEmpty();
    }

    @Test
    void findAllById() {
        given(repository.findAllById(List.of(ENTITY_ID))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(false);
        given(writeBuffer.getIfPresent(DOMAIN_ID_1)).willReturn(Optional.empty());

        assertThat(underTest.findAllById(List.of(ENTITY_ID))).containsExactly(domain1);
    }

    @Test
    void findAllById_inWriteBuffer() {
        given(repository.findAllById(List.of(ENTITY_ID))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(false);
        given(writeBuffer.getIfPresent(DOMAIN_ID_1)).willReturn(Optional.of(domain2));

        assertThat(underTest.findAllById(List.of(ENTITY_ID))).containsExactly(domain2);
    }

    @Test
    void findById_deleted() {
        given(idConverter.apply(ENTITY_ID)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(true);

        assertThat(underTest.findById(ENTITY_ID)).isEmpty();
    }

    @Test
    void findById_inWriteBuffer() {
        given(idConverter.apply(ENTITY_ID)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(false);
        given(writeBuffer.getIfPresent(DOMAIN_ID_1)).willReturn(Optional.of(domain1));

        assertThat(underTest.findById(ENTITY_ID)).contains(domain1);
    }

    @Test
    void findById_inRepository() {
        given(idConverter.apply(ENTITY_ID)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(false);
        given(writeBuffer.getIfPresent(DOMAIN_ID_1)).willReturn(Optional.empty());
        given(repository.findById(ENTITY_ID)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain1));

        assertThat(underTest.findById(ENTITY_ID)).contains(domain1);
    }

    @Test
    void save() {
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(listCacheKeyExtractor.apply(domain1)).willReturn(listCacheKey);

        underTest.save(domain1);

        then(deleteBuffer).should().remove(DOMAIN_ID_1);
        then(writeBuffer).should().add(DOMAIN_ID_1, domain1);
        then(readCache).should().invalidate(listCacheKey);
        then(repository).shouldHaveNoInteractions();
    }

    @Test
    void saveAll() {
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(listCacheKeyExtractor.apply(domain1)).willReturn(listCacheKey);

        underTest.saveAll(List.of(domain1));

        then(deleteBuffer).should().remove(DOMAIN_ID_1);
        then(writeBuffer).should().add(DOMAIN_ID_1, domain1);
        then(readCache).should().invalidate(listCacheKey);
        then(repository).shouldHaveNoInteractions();
    }

    @Test
    void searchOne_inWriteBuffer() {
        given(writeBuffer.search(search)).willReturn(List.of(domain1));
        given(readCache.asMap()).willReturn(new ConcurrentHashMap<>());
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(false);

        assertThat(underTest.searchOne(search, query, mergeFunction)).contains(domain1);
    }

    @Test
    void searchOne_inReadCache() {
        given(writeBuffer.search(search)).willReturn(List.of());
        ConcurrentHashMap<ListCacheKey, List<Domain>> map = new ConcurrentHashMap<>();
        map.put(listCacheKey, List.of(domain1));
        given(readCache.asMap()).willReturn(map);
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(false);
        given(search.apply(domain1)).willReturn(true);

        assertThat(underTest.searchOne(search, query, mergeFunction)).contains(domain1);
    }

    @Test
    void searchOne_deleted() {
        given(writeBuffer.search(search)).willReturn(List.of(domain1));
        given(readCache.asMap()).willReturn(new ConcurrentHashMap<>());
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(true);

        assertThat(underTest.searchOne(search, query, mergeFunction)).isEmpty();
    }

    @Test
    void searchOne_foundInRepository() {
        given(writeBuffer.search(search)).willReturn(List.of());
        given(readCache.asMap()).willReturn(new ConcurrentHashMap<>());
        given(query.get()).willReturn(Optional.of(entity));
        given(converter.convertEntity(entity)).willReturn(domain1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(false);
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);

        assertThat(underTest.searchOne(search, query, mergeFunction)).contains(domain1);
    }

    @Test
    void searchOne_foundInRepository_deleted() {
        given(writeBuffer.search(search)).willReturn(List.of());
        given(readCache.asMap()).willReturn(new ConcurrentHashMap<>());
        given(query.get()).willReturn(Optional.of(entity));
        given(converter.convertEntity(entity)).willReturn(domain1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(true);
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);

        assertThat(underTest.searchOne(search, query, mergeFunction)).isEmpty();
    }

    @Test
    void searchOne_mergeMultiple() {
        given(writeBuffer.search(search)).willReturn(List.of(domain1));
        ConcurrentHashMap<ListCacheKey, List<Domain>> map = new ConcurrentHashMap<>();
        map.put(listCacheKey, List.of(domain2));
        given(readCache.asMap()).willReturn(map);
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(domainIdExtractor.apply(domain2)).willReturn(DOMAIN_ID_1);
        given(search.apply(domain2)).willReturn(true);
        given(mergeFunction.apply(domain1, domain2)).willReturn(domain1);

        assertThat(underTest.searchOne(search, query, mergeFunction)).contains(domain1);
    }

    @Test
    void searchOne_multipleMatches() {
        given(writeBuffer.search(search)).willReturn(List.of(domain1));
        ConcurrentHashMap<ListCacheKey, List<Domain>> map = new ConcurrentHashMap<>();
        map.put(listCacheKey, List.of(domain2));
        given(readCache.asMap()).willReturn(map);
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(domainIdExtractor.apply(domain2)).willReturn(DOMAIN_ID_2);
        given(search.apply(domain2)).willReturn(true);

        assertThat(catchThrowable(() -> underTest.searchOne(search, query, mergeFunction)))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void searchList() {
        given(readCache.getIfPresent(listCacheKey)).willReturn(List.of(domain1));

        assertThat(underTest.searchList(listCacheKey, () -> List.of(entity))).containsExactly(domain1);
    }

    @Test
    void searchList_notCached() {
        given(readCache.getIfPresent(listCacheKey)).willReturn(null);
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(false);
        given(writeBuffer.getIfPresent(DOMAIN_ID_1)).willReturn(Optional.empty());
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));

        assertThat(underTest.searchList(listCacheKey, () -> List.of(entity))).containsExactly(domain1);
    }

    @Test
    void searchList_notCached_deleted() {
        given(readCache.getIfPresent(listCacheKey)).willReturn(null);
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(true);
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));

        assertThat(underTest.searchList(listCacheKey, () -> List.of(entity))).isEmpty();
    }

    @Test
    void searchList_updated() {
        given(readCache.getIfPresent(listCacheKey)).willReturn(null);
        given(domainIdExtractor.apply(domain1)).willReturn(DOMAIN_ID_1);
        given(deleteBuffer.contains(DOMAIN_ID_1)).willReturn(false);
        given(writeBuffer.getIfPresent(DOMAIN_ID_1)).willReturn(Optional.of(domain2));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));

        assertThat(underTest.searchList(listCacheKey, () -> List.of(entity))).containsExactly(domain2);
    }

    private static class ListCachedBufferedDaoImpl extends ListCachedBufferedDao<Entity, Domain, String, ListCacheKey, UUID, CrudRepository<Entity, String>> {
        private final Function<Domain, ListCacheKey> listCacheKeyExtractor;
        private final Function<String, UUID> idConverter;
        private final Function<Domain, UUID> domainIdExtractor;
        private final BiFunction<UUID, Domain, Boolean> idMatcher;

        @Builder
        protected ListCachedBufferedDaoImpl(
            Converter<Entity, Domain> converter,
            CrudRepository<Entity, String> repository,
            Cache<ListCacheKey, List<Domain>> readCache,
            WriteBuffer<UUID, Domain> writeBuffer,
            DeleteBuffer<UUID> deleteBuffer,
            Function<Domain, ListCacheKey> listCacheKeyExtractor,
            Function<String, UUID> idConverter,
            Function<Domain, UUID> domainIdExtractor,
            BiFunction<UUID, Domain, Boolean> idMatcher
        ) {
            super(converter, repository, readCache, writeBuffer, deleteBuffer);
            this.listCacheKeyExtractor = listCacheKeyExtractor;
            this.idConverter = idConverter;
            this.domainIdExtractor = domainIdExtractor;
            this.idMatcher = idMatcher;
        }

        @Override
        protected ListCacheKey getCacheKey(Domain domain) {
            return listCacheKeyExtractor.apply(domain);
        }

        @Override
        protected UUID toDomainId(String id) {
            return idConverter.apply(id);
        }

        @Override
        protected UUID getDomainId(Domain domain) {
            return domainIdExtractor.apply(domain);
        }

        @Override
        protected boolean matchesWithId(UUID domainId, Domain domain) {
            return idMatcher.apply(domainId, domain);
        }
    }

    private static class ListCacheKey {

    }

    private static class Entity {

    }

    private static class Domain {

    }
}