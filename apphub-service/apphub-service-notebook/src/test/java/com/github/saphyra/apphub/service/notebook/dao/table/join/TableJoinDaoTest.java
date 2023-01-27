package com.github.saphyra.apphub.service.notebook.dao.table.join;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TableJoinDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID TABLE_JOIN_ID = UUID.randomUUID();
    private static final String TABLE_JOIN_ID_STRING = "table-join-id";
    private static final UUID PARENT = UUID.randomUUID();
    private static final String PARENT_STRING = "parent";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private TableJoinConverter converter;

    @Mock
    private TableJoinRepository repository;

    @InjectMocks
    private TableJoinDao underTest;

    @Mock
    private TableJoinEntity entity;

    @Mock
    private TableJoin domain;

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByUserId(USER_ID_STRING);
    }

    @Test
    public void exists() {
        given(uuidConverter.convertDomain(TABLE_JOIN_ID)).willReturn(TABLE_JOIN_ID_STRING);
        given(repository.existsById(TABLE_JOIN_ID_STRING)).willReturn(true);

        boolean result = underTest.exists(TABLE_JOIN_ID);

        assertThat(result).isTrue();
    }

    @Test
    public void getByParent() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(repository.getByParent(PARENT_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<TableJoin> result = underTest.getByParent(PARENT);

        assertThat(result).containsExactly(domain);
    }
}