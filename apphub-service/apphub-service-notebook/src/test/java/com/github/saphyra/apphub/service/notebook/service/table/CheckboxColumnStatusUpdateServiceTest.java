package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CheckboxColumnStatusUpdateServiceTest {
    private static final UUID COLUMN_ID = UUID.randomUUID();

    @Mock
    private  ColumnTypeDao columnTypeDao;

    @Mock
    private  ContentDao contentDao;

    @InjectMocks
    private CheckboxColumnStatusUpdateService underTest;

    @Mock
    private ColumnTypeDto columnTypeDto;

    @Mock
    private Content content;

    @Test
    void nullStatus(){
        Throwable ex = catchThrowable(()-> underTest.updateColumnStatus(COLUMN_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "status", "must not be null");
    }

    @Test
    void notCheckboxColumn(){
        given(columnTypeDao.findByIdValidated(COLUMN_ID)).willReturn(columnTypeDto);
        given(columnTypeDto.getType()).willReturn(ColumnType.LINK);

        Throwable ex = catchThrowable(() -> underTest.updateColumnStatus(COLUMN_ID, true));

        ExceptionValidator.validateInvalidParam(ex, "columnId", "not a " + ColumnType.CHECKBOX);
    }

    @Test
    void updateColumnStatus(){
        given(columnTypeDao.findByIdValidated(COLUMN_ID)).willReturn(columnTypeDto);
        given(columnTypeDto.getType()).willReturn(ColumnType.CHECKBOX);
        given(contentDao.findByParentValidated(COLUMN_ID)).willReturn(content);

        underTest.updateColumnStatus(COLUMN_ID, true);

        then(content).should().setContent(String.valueOf(true));
        then(contentDao).should().save(content);
    }
}