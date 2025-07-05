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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CachedBufferedDaoTest {
    private static final UUID CACHE_KEY_1 = UUID.randomUUID();
    private static final UUID CACHE_KEY_2 = UUID.randomUUID();
    private static final String ENTITY_ID = "entity-id";

    @Mock
    private Converter<Entity, Domain> converter;

    @Mock
    private CrudRepository<Entity, String> repository;

    @Mock
    private Cache<UUID, Domain> readCache;

    @Mock
    private WriteBuffer<UUID, Domain> writeBuffer;

    @Mock
    private DeleteBuffer<UUID> deleteBuffer;

    @Mock
    private Function<String, UUID> cacheKeyConverter;

    @Mock
    private Function<Domain, UUID> cacheKeyExtractor;

    private CachedBufferedDaoImpl underTest;

    @Mock
    private Entity entity;

    @Mock
    private Domain domain1;

    @Mock
    private Domain domain2;

    @Mock
    private Function<Domain, Boolean> search;

    @Mock
    private Supplier<Optional<Entity>> query;

    @Mock
    private BiFunction<Domain, Domain, Domain> mergeFunction;

    @BeforeEach
    void setUp() {
        underTest = CachedBufferedDaoImpl.builder()
            .converter(converter)
            .repository(repository)
            .readCache(readCache)
            .writeBuffer(writeBuffer)
            .deleteBuffer(deleteBuffer)
            .cacheKeyConverter(cacheKeyConverter)
            .cacheKeyExtractor(cacheKeyExtractor)
            .build();
    }

    @Test
    void deleteDomain() {
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);

        underTest.delete(domain1);

        then(deleteBuffer).should().add(CACHE_KEY_1);
        then(readCache).should().invalidate(CACHE_KEY_1);
        then(writeBuffer).should().remove(CACHE_KEY_1);

        then(repository).shouldHaveNoInteractions();
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
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);

        underTest.deleteAll(List.of(domain1));

        then(deleteBuffer).should().add(CACHE_KEY_1);
        then(readCache).should().invalidate(CACHE_KEY_1);
        then(writeBuffer).should().remove(CACHE_KEY_1);

        then(repository).shouldHaveNoInteractions();
    }

    @Test
    void deleteById() {
        given(cacheKeyConverter.apply(ENTITY_ID)).willReturn(CACHE_KEY_1);

        underTest.deleteById(ENTITY_ID);

        then(readCache).should().invalidate(CACHE_KEY_1);
        then(writeBuffer).should().remove(CACHE_KEY_1);
        then(deleteBuffer).should().add(CACHE_KEY_1);

        then(repository).shouldHaveNoInteractions();
    }

    @Test
    void findAll_deleted() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(true);

        assertThat(underTest.findAll()).isEmpty();
    }

    @Test
    void findAll_inWriteCache() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);
        given(writeBuffer.getIfPresent(CACHE_KEY_1)).willReturn(Optional.of(domain2));

        assertThat(underTest.findAll()).containsExactly(domain2);

        then(readCache).should().putAll(Map.of(CACHE_KEY_1, domain2));
    }

    @Test
    void findAll_notInCaches() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);
        given(writeBuffer.getIfPresent(CACHE_KEY_1)).willReturn(Optional.empty());

        assertThat(underTest.findAll()).containsExactly(domain1);

        then(readCache).should().putAll(Map.of(CACHE_KEY_1, domain1));
    }

    @Test
    void findAllById_deleted() {
        given(repository.findAllById(List.of(ENTITY_ID))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(true);

        assertThat(underTest.findAllById(List.of(ENTITY_ID))).isEmpty();
    }

    @Test
    void findAllById_inWriteCache() {
        given(repository.findAllById(List.of(ENTITY_ID))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);
        given(writeBuffer.getIfPresent(CACHE_KEY_1)).willReturn(Optional.of(domain2));

        assertThat(underTest.findAllById(List.of(ENTITY_ID))).containsExactly(domain2);

        then(readCache).should().putAll(Map.of(CACHE_KEY_1, domain2));
    }

    @Test
    void findAllById_notInCaches() {
        given(repository.findAllById(List.of(ENTITY_ID))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);
        given(writeBuffer.getIfPresent(CACHE_KEY_1)).willReturn(Optional.empty());

        assertThat(underTest.findAllById(List.of(ENTITY_ID))).containsExactly(domain1);

        then(readCache).should().putAll(Map.of(CACHE_KEY_1, domain1));
    }

    @Test
    void findById_deleted() {
        given(cacheKeyConverter.apply(ENTITY_ID)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(true);

        assertThat(underTest.findById(ENTITY_ID)).isEmpty();

        then(readCache).shouldHaveNoInteractions();
        then(writeBuffer).shouldHaveNoInteractions();
        then(repository).shouldHaveNoInteractions();
    }

    @Test
    void findById_inWriteBuffer() {
        given(cacheKeyConverter.apply(ENTITY_ID)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);
        given(writeBuffer.getIfPresent(CACHE_KEY_1)).willReturn(Optional.of(domain1));

        assertThat(underTest.findById(ENTITY_ID)).contains(domain1);

        then(readCache).should().put(CACHE_KEY_1, domain1);
        then(repository).shouldHaveNoInteractions();
    }

    @Test
    void findById_inReadCache() {
        given(cacheKeyConverter.apply(ENTITY_ID)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);
        given(writeBuffer.getIfPresent(CACHE_KEY_1)).willReturn(Optional.empty());
        given(readCache.getIfPresent(CACHE_KEY_1)).willReturn(domain1);

        assertThat(underTest.findById(ENTITY_ID)).contains(domain1);

        then(readCache).should().put(CACHE_KEY_1, domain1);
        then(repository).shouldHaveNoInteractions();
    }

    @Test
    void findById_foundInRepository() {
        given(cacheKeyConverter.apply(ENTITY_ID)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);
        given(writeBuffer.getIfPresent(CACHE_KEY_1)).willReturn(Optional.empty());
        given(readCache.getIfPresent(CACHE_KEY_1)).willReturn(null);
        given(repository.findById(ENTITY_ID)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain1));

        assertThat(underTest.findById(ENTITY_ID)).contains(domain1);

        then(readCache).should().put(CACHE_KEY_1, domain1);
    }

    @Test
    void findById_notFound() {
        given(cacheKeyConverter.apply(ENTITY_ID)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);
        given(writeBuffer.getIfPresent(CACHE_KEY_1)).willReturn(Optional.empty());
        given(readCache.getIfPresent(CACHE_KEY_1)).willReturn(null);
        given(repository.findById(ENTITY_ID)).willReturn(Optional.empty());

        assertThat(underTest.findById(ENTITY_ID)).isEmpty();

        then(readCache).should(times(0)).putAll(any());
    }

    @Test
    void save() {
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);

        underTest.save(domain1);

        then(repository).shouldHaveNoInteractions();
        then(readCache).should().put(CACHE_KEY_1, domain1);
        then(writeBuffer).should().add(CACHE_KEY_1, domain1);
        then(deleteBuffer).should().remove(CACHE_KEY_1);
    }

    @Test
    void saveAll() {
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);

        underTest.saveAll(List.of(domain1));

        then(repository).shouldHaveNoInteractions();
        then(readCache).should().put(CACHE_KEY_1, domain1);
        then(writeBuffer).should().add(CACHE_KEY_1, domain1);
        then(deleteBuffer).should().remove(CACHE_KEY_1);
    }

    @Test
    void searchOne_foundInWriteBuffer() {
        given(writeBuffer.search(search)).willReturn(List.of(domain1));
        given(readCache.asMap()).willReturn(new ConcurrentHashMap<>());
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);

        assertThat(underTest.searchOne(search, query, mergeFunction)).contains(domain1);

        then(readCache).should().put(CACHE_KEY_1, domain1);
    }

    @Test
    void searchOne_foundInReadCache() {
        given(writeBuffer.search(search)).willReturn(List.of());
        ConcurrentHashMap<UUID, Domain> map = new ConcurrentHashMap<>();
        map.put(CACHE_KEY_1, domain1);
        given(readCache.asMap()).willReturn(map);
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);
        given(search.apply(domain1)).willReturn(true);

        assertThat(underTest.searchOne(search, query, mergeFunction)).contains(domain1);

        then(readCache).should().put(CACHE_KEY_1, domain1);
    }

    @Test
    void searchOne_deleted() {
        given(writeBuffer.search(search)).willReturn(List.of(domain1));
        given(readCache.asMap()).willReturn(new ConcurrentHashMap<>());
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(true);

        assertThat(underTest.searchOne(search, query, mergeFunction)).isEmpty();

        then(readCache).should(times(0)).put(any(), any());
    }

    @Test
    void searchOne_foundInRepository() {
        given(writeBuffer.search(search)).willReturn(List.of());
        given(readCache.asMap()).willReturn(new ConcurrentHashMap<>());
        given(query.get()).willReturn(Optional.of(entity));
        given(converter.convertEntity(entity)).willReturn(domain1);
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);

        assertThat(underTest.searchOne(search, query, mergeFunction)).contains(domain1);

        then(readCache).should().put(CACHE_KEY_1, domain1);
    }

    @Test
    void searchOne_foundInRepository_deleted() {
        given(writeBuffer.search(search)).willReturn(List.of());
        given(readCache.asMap()).willReturn(new ConcurrentHashMap<>());
        given(query.get()).willReturn(Optional.of(entity));
        given(converter.convertEntity(entity)).willReturn(domain1);
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(true);

        assertThat(underTest.searchOne(search, query, mergeFunction)).isEmpty();

        then(readCache).should(times(0)).put(any(), any());
    }

    @Test
    void searchOne_mergingMultiple() {
        given(writeBuffer.search(search)).willReturn(List.of(domain1));
        ConcurrentHashMap<UUID, Domain> map = new ConcurrentHashMap<>();
        map.put(CACHE_KEY_1, domain2);
        given(readCache.asMap()).willReturn(map);
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(cacheKeyExtractor.apply(domain2)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);
        given(search.apply(domain2)).willReturn(true);
        given(mergeFunction.apply(domain1, domain2)).willReturn(domain1);

        assertThat(underTest.searchOne(search, query, mergeFunction)).contains(domain1);

        then(readCache).should().put(CACHE_KEY_1, domain1);
    }

    @Test
    void searchOne_multipleFound() {
        given(writeBuffer.search(search)).willReturn(List.of(domain1));
        ConcurrentHashMap<UUID, Domain> map = new ConcurrentHashMap<>();
        map.put(CACHE_KEY_2, domain2);
        given(readCache.asMap()).willReturn(map);
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(cacheKeyExtractor.apply(domain2)).willReturn(CACHE_KEY_2);
        given(search.apply(domain2)).willReturn(true);

        assertThat(catchThrowable(() -> underTest.searchOne(search, query, mergeFunction)))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void search() {
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);
        given(writeBuffer.getIfPresent(CACHE_KEY_1)).willReturn(Optional.empty());

        assertThat(underTest.search(() -> List.of(entity))).containsExactly(domain1);

        then(readCache).should().putAll(Map.of(CACHE_KEY_1, domain1));
    }

    @Test
    void search_deleted() {
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(true);

        assertThat(underTest.search(() -> List.of(entity))).isEmpty();

        then(readCache).should().putAll(Map.of());
    }

    @Test
    void search_inWriteBuffer() {
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));
        given(cacheKeyExtractor.apply(domain1)).willReturn(CACHE_KEY_1);
        given(deleteBuffer.contains(CACHE_KEY_1)).willReturn(false);
        given(writeBuffer.getIfPresent(CACHE_KEY_1)).willReturn(Optional.of(domain2));

        assertThat(underTest.search(() -> List.of(entity))).containsExactly(domain2);

        then(readCache).should().putAll(Map.of(CACHE_KEY_1, domain2));
    }

    private static class CachedBufferedDaoImpl extends CachedBufferedDao<Entity, Domain, String, UUID, CrudRepository<Entity, String>> {
        private final Function<String, UUID> cacheKeyConverter;
        private final Function<Domain, UUID> cacheKeyExtractor;

        @Builder
        protected CachedBufferedDaoImpl(
            Converter<Entity, Domain> converter,
            CrudRepository<Entity, String> repository,
            Cache<UUID, Domain> readCache,
            WriteBuffer<UUID, Domain> writeBuffer,
            DeleteBuffer<UUID> deleteBuffer,
            Function<String, UUID> cacheKeyConverter,
            Function<Domain, UUID> cacheKeyExtractor
        ) {
            super(converter, repository, readCache, writeBuffer, deleteBuffer);
            this.cacheKeyConverter = cacheKeyConverter;
            this.cacheKeyExtractor = cacheKeyExtractor;
        }

        @Override
        protected UUID toCacheKey(String id) {
            return cacheKeyConverter.apply(id);
        }

        @Override
        protected UUID getCacheKey(Domain domain) {
            return cacheKeyExtractor.apply(domain);
        }
    }

    private static class Entity {

    }

    private static class Domain {

    }
}