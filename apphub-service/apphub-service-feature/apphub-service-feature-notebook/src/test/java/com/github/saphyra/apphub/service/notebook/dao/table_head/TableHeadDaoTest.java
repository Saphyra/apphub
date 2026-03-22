package com.github.saphyra.apphub.service.notebook.dao.table_head;


import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TableHeadDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();
    private static final String TABLE_HEAD_ID_STRING = "table-head-id";
    private static final UUID PARENT = UUID.randomUUID();
    private static final String PARENT_STRING = "parent";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private TableHeadConverter converter;

    @Mock
    private TableHeadRepository repository;

    private TableHeadDao underTest;

    @Mock
    private TableHeadEntity entity;

    @Mock
    private TableHead domain;

    @BeforeEach
    public void setUp() {
        underTest = new TableHeadDao(converter, repository, uuidConverter);
    }

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByUserId(USER_ID_STRING);
    }

    @Test
    public void exists() {
        given(uuidConverter.convertDomain(TABLE_HEAD_ID)).willReturn(TABLE_HEAD_ID_STRING);
        given(repository.existsById(TABLE_HEAD_ID_STRING)).willReturn(true);

        boolean result = underTest.exists(TABLE_HEAD_ID);

        assertThat(result).isTrue();
    }

    @Test
    public void getByParent() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(repository.getByParent(PARENT_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<TableHead> result = underTest.getByParent(PARENT);

        Assertions.assertThat(result).containsExactly(domain);
    }

    @Test
    void deleteById() {
        given(uuidConverter.convertDomain(TABLE_HEAD_ID)).willReturn(TABLE_HEAD_ID_STRING);
        given(repository.existsById(TABLE_HEAD_ID_STRING)).willReturn(true);

        underTest.deleteById(TABLE_HEAD_ID);

        then(repository).should().deleteById(TABLE_HEAD_ID_STRING);
    }

    @Test
    void findById() {
        given(uuidConverter.convertDomain(TABLE_HEAD_ID)).willReturn(TABLE_HEAD_ID_STRING);
        given(repository.findById(TABLE_HEAD_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findById(TABLE_HEAD_ID)).contains(domain);

    }
}