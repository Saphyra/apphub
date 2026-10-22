package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.common.NotebookConstants;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class CreateTableHeadValidatorTest {
    @InjectMocks
    private CreateTableHeadValidator underTest;

    @Test
    void nullTableHeads() {
        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(null));

        ExceptionValidator.validateInvalidParam(ex, "tableHeads", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void nullColumnIndex() {
        TableHeadModel model = TableHeadModel.builder()
            .content("adf")
            .columnIndex(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "tableHead.columnIndex", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void blankContent() {
        TableHeadModel model = TableHeadModel.builder()
            .content(" ")
            .columnIndex(324)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "tableHead.content", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    void tooLongContent() {
        TableHeadModel model = TableHeadModel.builder()
            .content("A".repeat(NotebookConstants.MAX_CONTENT_LENGTH + 1))
            .columnIndex(324)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "tableHead.content", ValidationUtil.TOO_LONG);
    }

    @Test
    void valid() {
        TableHeadModel model = TableHeadModel.builder()
            .content("adf")
            .columnIndex(34)
            .build();

        underTest.validateTableHeads(List.of(model));
    }
}