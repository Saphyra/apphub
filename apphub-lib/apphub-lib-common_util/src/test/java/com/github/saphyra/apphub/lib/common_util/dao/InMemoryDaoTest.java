package com.github.saphyra.apphub.lib.common_util.dao;

import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import com.github.saphyra.apphub.test.common.ReflectionUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;

@ExtendWith(MockitoExtension.class)
class InMemoryDaoTest {
    private static final String KEY_1 = "key-1";
    private static final String KEY_2 = "key-2";
    private static final Integer VALUE_1 = 234;
    private static final Integer VALUE_2 = 236;

    @Mock
    private Converter<Entity, Entity> converter;

    @Mock
    private CrudRepository<Entity, String> repository;

    @InjectMocks
    private InMemoryDaoImpl underTest;

    @BeforeEach
    void setUp() {
        lenient()
            .when(converter.convertEntity(anyList()))
            .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void delete() throws NoSuchFieldException, IllegalAccessException {
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        given(repository.findAll()).willReturn(List.of(entity));
        underTest.load();
        given(converter.convertDomain(entity)).willReturn(entity);

        underTest.delete(entity);

        then(repository).should().delete(entity);
        assertThat(getCache()).isEmpty();
    }

    @Test
    void deleteAll() throws NoSuchFieldException, IllegalAccessException {
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        given(repository.findAll()).willReturn(List.of(entity));
        underTest.load();

        underTest.deleteAll();

        then(repository).should().deleteAll();
        assertThat(getCache()).isEmpty();
    }

    @Test
    void deleteAllList() throws NoSuchFieldException, IllegalAccessException {
        Entity entity1 = new Entity(KEY_1, VALUE_1, false);
        Entity entity2 = new Entity(KEY_2, VALUE_2, false);
        given(repository.findAll()).willReturn(List.of(entity1, entity2));
        underTest.load();
        given(converter.convertDomain(List.of(entity1))).willReturn(List.of(entity1));

        underTest.deleteAll(List.of(entity1));

        assertThat(getCache()).containsEntry(KEY_2, entity2);
        then(repository).should().deleteAll(List.of(entity1));
    }

    @Test
    void deleteById() throws NoSuchFieldException, IllegalAccessException {
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        given(repository.findAll()).willReturn(List.of(entity));
        underTest.load();
        given(repository.existsById(KEY_1)).willReturn(true);

        underTest.deleteById(KEY_1);

        assertThat(getCache()).isEmpty();
        then(repository).should().deleteById(KEY_1);
    }

    @Test
    void findAll() throws NoSuchFieldException, IllegalAccessException {
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        given(repository.findAll()).willReturn(List.of(entity));

        assertThat(underTest.findAll()).containsExactly(entity);

        assertThat(getCache()).containsEntry(KEY_1, entity);
    }

    @Test
    void findAllById() throws NoSuchFieldException, IllegalAccessException {
        Entity entity1 = new Entity(KEY_1, VALUE_1, false);
        Entity entity2 = new Entity(KEY_2, VALUE_2, false);
        given(repository.findAll()).willReturn(List.of(entity1));
        underTest.load();

        given(repository.findAllById(List.of(KEY_2))).willReturn(List.of(entity2));

        assertThat(underTest.findAllById(List.of(KEY_1, KEY_2))).containsExactlyInAnyOrder(entity1, entity2);

        assertThat(getCache())
            .hasSize(2)
            .containsEntry(KEY_1, entity1)
            .containsEntry(KEY_2, entity2);
    }

    @Test
    void findById_loadFromCache() {
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        given(repository.findAll()).willReturn(List.of(entity));
        underTest.load();
        reset(repository);

        assertThat(underTest.findById(KEY_1)).contains(entity);
        then(repository).shouldHaveNoInteractions();
    }

    @Test
    void findById_loadToCache() throws NoSuchFieldException, IllegalAccessException {
        Entity entity = new Entity(KEY_1, VALUE_1, false);
        given(repository.findById(KEY_1)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(entity));

        assertThat(underTest.findById(KEY_1)).contains(entity);

        assertThat(getCache()).containsEntry(KEY_1, entity);
    }

    @Test
    void save() throws NoSuchFieldException, IllegalAccessException {
        Entity entity = new Entity(KEY_1, VALUE_1, true);
        given(converter.convertDomain(entity)).willReturn(entity);

        underTest.save(entity);

        then(repository).should().save(entity);
        assertThat(getCache()).containsEntry(KEY_1, entity);
    }

    @Test
    void save_shouldNotSave() throws NoSuchFieldException, IllegalAccessException {
        Entity entity = new Entity(KEY_1, VALUE_1, false);

        underTest.save(entity);

        then(repository).shouldHaveNoInteractions();
        assertThat(getCache()).isEmpty();
    }

    @Test
    void saveAll() throws NoSuchFieldException, IllegalAccessException {
        Entity entity1 = new Entity(KEY_1, VALUE_1, true);
        Entity entity2 = new Entity(KEY_2, VALUE_2, false);
        given(converter.convertDomain(List.of(entity1))).willReturn(List.of(entity1));

        underTest.saveAll(List.of(entity1, entity2));

        then(repository).should().saveAll(List.of(entity1));
        assertThat(getCache()).containsEntry(KEY_1, entity1);
    }

    private Map<String, Entity> getCache() throws IllegalAccessException, NoSuchFieldException {
        return ReflectionUtils.getFieldValue(underTest, "cache");
    }

    static class InMemoryDaoImpl extends InMemoryDao<Entity, Entity, String, CrudRepository<Entity, String>> {
        InMemoryDaoImpl(Converter<Entity, Entity> converter, CrudRepository<Entity, String> repository) {
            super(converter, repository);
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