package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.CrudRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AbstractDaoTest {
    private static final String ID = "id";

    @Mock
    private ConverterBase<Entity, Domain> converter;

    @Mock
    private CrudRepository<Entity, String> repository;

    @InjectMocks
    private AbstractDaoImpl underTest;

    @Mock
    private Entity entity;

    @Mock
    private Domain domain;

    @Test
    public void deleteDomain() {
        given(converter.convertDomain(domain)).willReturn(entity);

        underTest.delete(domain);

        verify(repository).delete(entity);
    }

    @Test
    public void deleteAll() {
        underTest.deleteAll();

        verify(repository).deleteAll();
    }

    @Test
    public void deleteAllIn() {
        given(converter.convertDomain(Arrays.asList(domain))).willReturn(Arrays.asList(entity));

        underTest.deleteAll(Arrays.asList(domain));

        verify(repository).deleteAll(Arrays.asList(entity));
    }

    @Test
    public void deleteById_exists() {
        given(repository.existsById(ID)).willReturn(true);

        underTest.deleteById(ID);

        verify(repository).deleteById(ID);
    }

    @Test
    public void deleteById_doesNotExist() {
        given(repository.existsById(ID)).willReturn(false);

        underTest.deleteById(ID);

        verify(repository, times(0)).deleteById(ID);
    }

    @Test
    public void findAll() {
        given(repository.findAll()).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<Domain> result = underTest.findAll();

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void findAllById() {
        given(repository.findAllById(Arrays.asList(ID))).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<Domain> result = underTest.findAllById(Arrays.asList(ID));

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void findById() {
        given(repository.findById(ID)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<Domain> result = underTest.findById(ID);

        assertThat(result).contains(domain);
    }

    @Test
    public void save() {
        given(converter.convertDomain(domain)).willReturn(entity);

        underTest.save(domain);

        verify(repository).save(entity);
    }

    @Test
    public void saveAll() {
        given(converter.convertDomain(Arrays.asList(domain))).willReturn(Arrays.asList(entity));

        underTest.saveAll(Arrays.asList(domain));

        verify(repository).saveAll(Arrays.asList(entity));
    }

    private static class Entity {

    }

    private static class Domain {

    }

    private static class AbstractDaoImpl extends AbstractDao<Entity, Domain, String, CrudRepository<Entity, String>> {

        public AbstractDaoImpl(Converter<Entity, Domain> converter, CrudRepository<Entity, String> repository) {
            super(converter, repository);
        }
    }
}