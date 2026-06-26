package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.common.NotebookConstants;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class EditTableHeadValidatorTest {
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();

    @InjectMocks
    private EditTableHeadValidator underTest;

    @Test
    void nullTableHeads() {
        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(null));

        ExceptionValidator.validateInvalidParam(ex, "tableHeads", "must not be null");
    }

    @Test
    void nullColumnIndex() {
        TableHeadModel model = TableHeadModel.builder()
            .columnIndex(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "tableHead.columnIndex", "must not be null");
    }

    @Test
    void blankContent() {
        TableHeadModel model = TableHeadModel.builder()
            .columnIndex(3214)
            .content(" ")
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "tableHead.content", "must not be null or blank");
    }

    @Test
    void tooLongContent() {
        TableHeadModel model = TableHeadModel.builder()
            .columnIndex(3214)
            .content("a".repeat(NotebookConstants.MAX_CONTENT_LENGTH + 1))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "tableHead.content", "too long");
    }

    @Test
    void nullType() {
        TableHeadModel model = TableHeadModel.builder()
            .columnIndex(3214)
            .content("a")
            .type(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "tableHead.type", "must not be null");
    }

    @Test
    void nullTableHeadIdWhenExisting() {
        TableHeadModel model = TableHeadModel.builder()
            .tableHeadId(null)
            .columnIndex(3214)
            .content("a")
            .type(ItemType.EXISTING)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "tableHead.tableHeadId", "must not be null");
    }

    @Test
    void valid() {
        TableHeadModel model = TableHeadModel.builder()
            .tableHeadId(TABLE_HEAD_ID)
            .columnIndex(3214)
            .content("a")
            .type(ItemType.EXISTING)
            .build();

        underTest.validateTableHeads(List.of(model));
    }
}