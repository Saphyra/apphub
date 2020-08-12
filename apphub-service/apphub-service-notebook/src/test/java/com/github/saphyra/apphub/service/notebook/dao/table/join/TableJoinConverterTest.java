package com.github.saphyra.apphub.service.notebook.dao.table.join;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@RunWith(MockitoJUnitRunner.class)
public class TableJoinConverterTest {
    private static final String TABLE_JOIN_ID_STRING = "table-join-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String PARENT_STRING = "parent";
    private static final Integer ROW_INDEX = 23456;
    private static final Integer COLUMN_INDEX = 3567;
    private static final UUID TABLE_JOIN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private TableJoinConverter underTest;

    @Test
    public void convertEntity() {
        TableJoinEntity entity = TableJoinEntity.builder()
            .tableJoinId(TABLE_JOIN_ID_STRING)
            .userId(USER_ID_STRING)
            .parent(PARENT_STRING)
            .rowIndex(ROW_INDEX)
            .columnIndex(COLUMN_INDEX)
            .build();
        given(uuidConverter.convertEntity(TABLE_JOIN_ID_STRING)).willReturn(TABLE_JOIN_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(PARENT_STRING)).willReturn(PARENT);

        TableJoin result = underTest.convertEntity(entity);

        assertThat(result.getTableJoinId()).isEqualTo(TABLE_JOIN_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getRowIndex()).isEqualTo(ROW_INDEX);
        assertThat(result.getColumnIndex()).isEqualTo(COLUMN_INDEX);
    }

    @Test
    public void convertDomain() {
        TableJoin domain = TableJoin.builder()
            .tableJoinId(TABLE_JOIN_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .rowIndex(ROW_INDEX)
            .columnIndex(COLUMN_INDEX)
            .build();
        given(uuidConverter.convertDomain(TABLE_JOIN_ID)).willReturn(TABLE_JOIN_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);

        TableJoinEntity result = underTest.convertDomain(domain);

        assertThat(result.getTableJoinId()).isEqualTo(TABLE_JOIN_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getParent()).isEqualTo(PARENT_STRING);
        assertThat(result.getRowIndex()).isEqualTo(ROW_INDEX);
        assertThat(result.getColumnIndex()).isEqualTo(COLUMN_INDEX);
    }
}