package com.github.saphyra.apphub.service.notebook.service.table.edition;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.service.TitleValidator;
import com.github.saphyra.apphub.service.notebook.service.table.ColumnNameValidator;
import com.github.saphyra.apphub.service.notebook.service.table.RowValidator;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EditTableRequestValidatorTest {
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();
    private static final String NEW_TABLE_HEAD = "new-table-head";
    private static final UUID TABLE_JOIN_ID = UUID.randomUUID();
    private static final String NEW_VALUE = "new-value";
    private static final String NEW_TITLE = "new-title";

    @Mock
    private TitleValidator titleValidator;

    @Mock
    private ColumnNameValidator columnNameValidator;

    @Mock
    private RowValidator rowValidator;

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private TableJoinDao tableJoinDao;

    @InjectMocks
    private EditTableRequestValidator underTest;

    @After
    public void v() {
        verify(columnNameValidator).validate(NEW_TABLE_HEAD);
        verify(rowValidator).validate(Arrays.asList(NEW_VALUE), 1);
        verify(titleValidator).validate(NEW_TITLE);
    }

    @Test
    public void tableHeadDoesNotExist() {
        given(tableHeadDao.exists(TABLE_HEAD_ID)).willReturn(false);

        EditTableRequest request = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(new KeyValuePair<>(TABLE_HEAD_ID, NEW_TABLE_HEAD)))
            .columns(Arrays.asList(Arrays.asList(new KeyValuePair<>(TABLE_JOIN_ID, NEW_VALUE))))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
    }

    @Test
    public void tableJoinDoesNotExist() {
        given(tableHeadDao.exists(TABLE_HEAD_ID)).willReturn(true);
        given(tableJoinDao.exists(TABLE_JOIN_ID)).willReturn(false);

        EditTableRequest request = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(new KeyValuePair<>(TABLE_HEAD_ID, NEW_TABLE_HEAD)))
            .columns(Arrays.asList(Arrays.asList(new KeyValuePair<>(TABLE_JOIN_ID, NEW_VALUE))))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
    }

    @Test
    public void valid() {
        given(tableHeadDao.exists(TABLE_HEAD_ID)).willReturn(true);
        given(tableJoinDao.exists(TABLE_JOIN_ID)).willReturn(true);

        EditTableRequest request = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(new KeyValuePair<>(TABLE_HEAD_ID, NEW_TABLE_HEAD)))
            .columns(Arrays.asList(Arrays.asList(new KeyValuePair<>(TABLE_JOIN_ID, NEW_VALUE))))
            .build();

        underTest.validate(request);

        //No exception
    }
}