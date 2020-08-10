package com.github.saphyra.apphub.service.notebook.dao.table.head;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;

@RunWith(MockitoJUnitRunner.class)
public class TableHeadConverterTest {
    private static final String TABLE_HEAD_ID_STING = "table-head-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String PARENT_STRING = "parent";
    private static final Integer COLUMN_INDEX = 2341;
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private TableHeadConverter underTest;

    @Test
    public void convertEntity() {
        TableHeadEntity entity = TableHeadEntity.builder()
            .tableHeadId(TABLE_HEAD_ID_STING)
            .userId(USER_ID_STRING)
            .parent(PARENT_STRING)
            .columnIndex(COLUMN_INDEX)
            .build();
        given(uuidConverter.convertEntity(TABLE_HEAD_ID_STING)).willReturn(TABLE_HEAD_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(PARENT_STRING)).willReturn(PARENT);

        TableHead result = underTest.convertEntity(entity);

        assertThat(result.getTableHeadId()).isEqualTo(TABLE_HEAD_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getColumnIndex()).isEqualTo(COLUMN_INDEX);
    }

    @Test
    public void convertDomain() {
        TableHead domain = TableHead.builder()
            .tableHeadId(TABLE_HEAD_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .columnIndex(COLUMN_INDEX)
            .build();
        given(uuidConverter.convertDomain(TABLE_HEAD_ID)).willReturn(TABLE_HEAD_ID_STING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);

        TableHeadEntity result = underTest.convertDomain(domain);

        assertThat(result.getTableHeadId()).isEqualTo(TABLE_HEAD_ID_STING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getParent()).isEqualTo(PARENT_STRING);
        assertThat(result.getColumnIndex()).isEqualTo(COLUMN_INDEX);
    }
}