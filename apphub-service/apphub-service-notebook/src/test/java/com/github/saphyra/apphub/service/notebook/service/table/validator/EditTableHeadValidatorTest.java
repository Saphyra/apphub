package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EditTableHeadValidatorTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();

    @Mock
    private TableHeadDao tableHeadDao;

    @InjectMocks
    private EditTableHeadValidator underTest;

    @Mock
    private TableHead tableHead;

    @Test
    void nullTableHeads() {
        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(LIST_ITEM_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "tableHeads", "must not be null");
    }

    @Test
    void nullColumnIndex() {
        TableHeadModel model = TableHeadModel.builder()
            .columnIndex(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(LIST_ITEM_ID, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "tableHead.columnIndex", "must not be null");
    }

    @Test
    void blankContent() {
        TableHeadModel model = TableHeadModel.builder()
            .columnIndex(3214)
            .content(" ")
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(LIST_ITEM_ID, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "tableHead.content", "must not be null or blank");
    }

    @Test
    void nullType() {
        TableHeadModel model = TableHeadModel.builder()
            .columnIndex(3214)
            .content("a")
            .type(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(LIST_ITEM_ID, List.of(model)));

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

        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(LIST_ITEM_ID, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "tableHead.tableHeadId", "must not be null");
    }

    @Test
    void differentParentWhenExisting() {
        TableHeadModel model = TableHeadModel.builder()
            .tableHeadId(TABLE_HEAD_ID)
            .columnIndex(3214)
            .content("a")
            .type(ItemType.EXISTING)
            .build();

        given(tableHeadDao.findByIdValidated(TABLE_HEAD_ID)).willReturn(tableHead);
        given(tableHead.getParent()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.validateTableHeads(LIST_ITEM_ID, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "tableHead.tableHeadId", "points to different table");


    }

    @Test
    void valid() {
        TableHeadModel model = TableHeadModel.builder()
            .tableHeadId(TABLE_HEAD_ID)
            .columnIndex(3214)
            .content("a")
            .type(ItemType.EXISTING)
            .build();

        given(tableHeadDao.findByIdValidated(TABLE_HEAD_ID)).willReturn(tableHead);
        given(tableHead.getParent()).willReturn(LIST_ITEM_ID);

        underTest.validateTableHeads(LIST_ITEM_ID, List.of(model));
    }
}