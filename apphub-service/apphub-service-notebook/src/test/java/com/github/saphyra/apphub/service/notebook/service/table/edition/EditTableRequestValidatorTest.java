package com.github.saphyra.apphub.service.notebook.service.table.edition;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.service.validator.TitleValidator;
import com.github.saphyra.apphub.service.notebook.service.table.ColumnNameValidator;
import com.github.saphyra.apphub.service.notebook.service.table.RowValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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

    @AfterEach
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

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.LIST_ITEM_NOT_FOUND);
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

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.LIST_ITEM_NOT_FOUND);
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