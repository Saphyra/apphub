package com.github.saphyra.apphub.service.notebook.dao.table.head;


import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
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

    @Before
    public void setUp(){
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

        assertThat(result).containsExactly(domain);
    }
}