package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.CrudRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StaticCachedDaoTest {
    private static final String FIELD_CACHE = "cache";
    private static final String KEY_1 = "key-1";
    private static final String KEY_2 = "key-2";
    private static final Integer VALUE_1 = 234;
    private static final Integer VALUE_2 = 236;

    @Mock
    private Converter<Entity, Entity> converter;

    @Mock
    private CrudRepository<Entity, String> repository;

    @InjectMocks
    private StaticCachedDaoImpl underTest;

    @Test
    void delete() throws IllegalAccessException {
        Map<String, Entity> cache = new HashMap<>();
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        cache.put(KEY_1, entity);
        FieldUtils.writeField(underTest, FIELD_CACHE, cache, true);
        given(converter.convertDomain(entity)).willReturn(entity);

        underTest.delete(entity);

        assertThat(cache).isEmpty();
        then(repository).should().delete(entity);
    }

    @Test
    void deleteAll() throws IllegalAccessException {
        Map<String, Entity> cache = new HashMap<>();
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        cache.put(KEY_1, entity);
        FieldUtils.writeField(underTest, FIELD_CACHE, cache, true);

        underTest.deleteAll();

        assertThat(cache).isEmpty();
        then(repository).should().deleteAll();
    }

    @Test
    void deleteAllList() throws IllegalAccessException {
        Map<String, Entity> cache = new HashMap<>();
        Entity entity1 = new Entity(KEY_1, VALUE_1, false);
        cache.put(KEY_1, entity1);
        Entity entity2 = new Entity(KEY_2, VALUE_2, false);
        cache.put(KEY_2, entity2);
        FieldUtils.writeField(underTest, FIELD_CACHE, cache, true);
        given(converter.convertDomain(List.of(entity1))).willReturn(List.of(entity1));

        underTest.deleteAll(List.of(entity1));

        assertThat(cache).hasSize(1).containsEntry(KEY_2, entity2);
        then(repository).should().deleteAll(List.of(entity1));
    }

    @Test
    void deleteById() throws IllegalAccessException {
        Map<String, Entity> cache = new HashMap<>();
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        cache.put(KEY_1, entity);
        FieldUtils.writeField(underTest, FIELD_CACHE, cache, true);
        given(repository.existsById(KEY_1)).willReturn(true);

        underTest.deleteById(KEY_1);

        assertThat(cache).isEmpty();
        then(repository).should().deleteById(KEY_1);
    }

    @Test
    void findAll() throws IllegalAccessException {
        Map<String, Entity> cache = new HashMap<>();
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        cache.put(KEY_1, entity);
        FieldUtils.writeField(underTest, FIELD_CACHE, cache, true);
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(entity));

        assertThat(underTest.findAll()).containsExactly(entity);

        assertThat(cache).containsEntry(KEY_1, entity);
    }

    @Test
    void findAllById() throws IllegalAccessException {
        Map<String, Entity> cache = new HashMap<>();
        Entity entity1 = new Entity(KEY_1, VALUE_1, false);
        cache.put(KEY_1, entity1);
        Entity entity2 = new Entity(KEY_2, VALUE_2, false);
        FieldUtils.writeField(underTest, FIELD_CACHE, cache, true);
        given(repository.findAllById(List.of(KEY_2))).willReturn(List.of(entity2));
        given(converter.convertEntity(List.of(entity2))).willReturn(List.of(entity2));

        assertThat(underTest.findAllById(List.of(KEY_1, KEY_2))).containsExactlyInAnyOrder(entity1, entity2);

        assertThat(cache)
            .containsEntry(KEY_1, entity1)
            .containsEntry(KEY_2, entity2);
    }

    @Test
    void findById_loadFromCache() throws IllegalAccessException {
        Map<String, Entity> cache = new HashMap<>();
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        cache.put(KEY_1, entity);
        FieldUtils.writeField(underTest, FIELD_CACHE, cache, true);

        assertThat(underTest.findById(KEY_1)).contains(entity);

        then(repository).shouldHaveNoInteractions();
    }

    @Test
    void findById_loadFToCache() throws IllegalAccessException {
        Map<String, Entity> cache = new HashMap<>();
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        FieldUtils.writeField(underTest, FIELD_CACHE, cache, true);
        given(repository.findById(KEY_1)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(entity));

        assertThat(underTest.findById(KEY_1)).contains(entity);

        assertThat(cache).containsEntry(KEY_1, entity);
    }

    @Test
    void save() throws IllegalAccessException {
        Map<String, Entity> cache = new HashMap<>();
        Entity entity = new Entity(KEY_1, VALUE_1, true);
        FieldUtils.writeField(underTest, FIELD_CACHE, cache, true);
        given(converter.convertDomain(entity)).willReturn(entity);

        underTest.save(entity);

        then(repository).should().save(entity);
        assertThat(cache).containsEntry(KEY_1, entity);
    }

    @Test
    void save_shouldNotSave() throws IllegalAccessException {
        Map<String, Entity> cache = new HashMap<>();
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        FieldUtils.writeField(underTest, FIELD_CACHE, cache, true);

        underTest.save(entity);

        then(repository).shouldHaveNoInteractions();
        assertThat(cache).isEmpty();
    }

    @Test
    void saveAll() throws IllegalAccessException {
        Entity entity1 = new Entity(KEY_1, VALUE_1, true);
        Entity entity2 = new Entity(KEY_2, VALUE_2, false);
        given(converter.convertDomain(List.of(entity1))).willReturn(List.of(entity1));
        Map<String, Entity> cache = new HashMap<>();
        FieldUtils.writeField(underTest, FIELD_CACHE, cache, true);

        underTest.saveAll(List.of(entity1, entity2));

        then(repository).should().saveAll(List.of(entity1));
        assertThat(cache).hasSize(1).containsEntry(KEY_1, entity1);
    }

    static class StaticCachedDaoImpl extends StaticCachedDao<Entity, Entity, String, CrudRepository<Entity, String>> {
        StaticCachedDaoImpl(Converter<Entity, Entity> converter, CrudRepository<Entity, String> repository) {
            super(converter, repository, false);
        }

        @Override
        protected String extractId(Entity entity) {
            return entity.key;
        }

        @Override
        protected boolean shouldSave(Entity entity) {
            return entity.shouldSave;
        }
    }

    @AllArgsConstructor
    @Data
    static class Entity {
        private String key;
        private Integer value;
        private Boolean shouldSave;
    }

}