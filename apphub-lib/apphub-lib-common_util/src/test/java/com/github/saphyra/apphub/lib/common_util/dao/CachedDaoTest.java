package com.github.saphyra.apphub.lib.common_util.dao;

import com.github.saphyra.apphub.lib.common_util.cache.AbstractCache;
import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import com.github.saphyra.apphub.lib.common_util.dao.CachedDao;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CachedDaoTest {
    private static final String KEY_1 = "key-1";
    private static final String KEY_2 = "key-2";
    private static final Integer VALUE_1 = 234;
    private static final Integer VALUE_2 = 236;

    @Mock
    private Converter<Entity, Entity> converter;

    @Mock
    private CrudRepository<Entity, String> repository;

    @Mock
    private AbstractCache<String, Entity> cache;

    @InjectMocks
    private CachedDaoImpl underTest;

    @Test
    void delete() {
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        given(converter.convertDomain(entity)).willReturn(entity);

        underTest.delete(entity);

        then(cache).should().invalidate(KEY_1);
        then(repository).should().delete(entity);
    }

    @Test
    void deleteAll() {
        underTest.deleteAll();

        then(cache).should().clear();
        then(repository).should().deleteAll();
    }

    @Test
    void deleteAllList() {
        Entity entity1 = new Entity(KEY_1, VALUE_1, false);
        given(converter.convertDomain(List.of(entity1))).willReturn(List.of(entity1));

        underTest.deleteAll(List.of(entity1));

        then(cache).should().invalidate(KEY_1);
        then(repository).should().deleteAll(List.of(entity1));
    }

    @Test
    void deleteById() {
        given(repository.existsById(KEY_1)).willReturn(true);

        underTest.deleteById(KEY_1);

        then(cache).should().invalidate(KEY_1);
        then(repository).should().deleteById(KEY_1);
    }

    @Test
    void findAll() {
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(entity));

        assertThat(underTest.findAll()).containsExactly(entity);

        then(cache).should().put(KEY_1, entity);
    }

    @Test
    void findAllById() {
        Entity entity1 = new Entity(KEY_1, VALUE_1, false);
        Entity entity2 = new Entity(KEY_2, VALUE_2, false);
        given(repository.findAllById(List.of(KEY_2))).willReturn(List.of(entity2));
        given(converter.convertEntity(List.of(entity2))).willReturn(List.of(entity2));
        given(cache.getIfPresent(KEY_1)).willReturn(Optional.of(entity1));
        given(cache.getIfPresent(KEY_2)).willReturn(Optional.empty());

        assertThat(underTest.findAllById(List.of(KEY_1, KEY_2))).containsExactlyInAnyOrder(entity1, entity2);

        then(cache).should().put(KEY_2, entity2);
    }

    @Test
    void findById_loadFromCache() {
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        given(cache.getIfPresent(KEY_1)).willReturn(Optional.of(entity));

        assertThat(underTest.findById(KEY_1)).contains(entity);

        then(repository).shouldHaveNoInteractions();
    }

    @Test
    void findById_loadToCache() {
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        given(cache.getIfPresent(KEY_1)).willReturn(Optional.empty());
        given(repository.findById(KEY_1)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(entity));

        assertThat(underTest.findById(KEY_1)).contains(entity);

        then(cache).should().put(KEY_1, entity);
    }

    @Test
    void save() {
        Entity entity = new Entity(KEY_1, VALUE_1, true);
        given(converter.convertDomain(entity)).willReturn(entity);

        underTest.save(entity);

        then(repository).should().save(entity);
        then(cache).should().put(KEY_1, entity);
    }

    @Test
    void save_shouldNotSave() {
        Entity entity = new Entity(KEY_1, VALUE_1, false);

        underTest.save(entity);

        then(repository).shouldHaveNoInteractions();
        then(cache).shouldHaveNoInteractions();
    }

    @Test
    void saveAll() {
        Entity entity1 = new Entity(KEY_1, VALUE_1, true);
        Entity entity2 = new Entity(KEY_2, VALUE_2, false);
        given(converter.convertDomain(List.of(entity1))).willReturn(List.of(entity1));

        underTest.saveAll(List.of(entity1, entity2));

        then(repository).should().saveAll(List.of(entity1));
        then(cache).should().put(KEY_1, entity1);
        then(cache).shouldHaveNoMoreInteractions();
    }

    static class CachedDaoImpl extends CachedDao<Entity, Entity, String, CrudRepository<Entity, String>> {
        CachedDaoImpl(Converter<Entity, Entity> converter, CrudRepository<Entity, String> repository, AbstractCache<String, Entity> cache) {
            super(converter, repository, false, cache);
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