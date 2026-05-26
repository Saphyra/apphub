package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.TitleValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistValidatorTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final String CONTENT = "content";
    private static final int INDEX = 0;
    private static final boolean CHECKED = false;

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @Mock
    private TitleValidator titleValidator;

    @InjectMocks
    private ChecklistValidator underTest;

    @Test
    void validate_valid() {
        CreateChecklistRequest request = CreateChecklistRequest.builder()
            .parent(PARENT)
            .title(TITLE)
            .items(List.of(validNewItem()))
            .build();

        underTest.validate(USER_ID, request);

        then(listItemRequestValidator).should().validate(USER_ID, TITLE, PARENT);
    }

    @Test
    void validateNew_itemsNull() {
        Throwable ex = catchThrowable(() -> underTest.validateNew(null));

        ExceptionValidator.validateInvalidParam(ex, "items", "must not be null");
    }

    @Test
    void validateNew_itemContentNull() {
        ChecklistItemModel item = ChecklistItemModel.builder()
            .content(null)
            .checked(CHECKED)
            .index(INDEX)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateNew(List.of(item)));

        ExceptionValidator.validateInvalidParam(ex, "item.content", "must not be null");
    }

    @Test
    void validateNew_itemCheckedNull() {
        ChecklistItemModel item = ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(null)
            .index(INDEX)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateNew(List.of(item)));

        ExceptionValidator.validateInvalidParam(ex, "item.checked", "must not be null");
    }

    @Test
    void validateNew_itemIndexNull() {
        ChecklistItemModel item = ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(CHECKED)
            .index(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateNew(List.of(item)));

        ExceptionValidator.validateInvalidParam(ex, "item.index", "must not be null");
    }

    @Test
    void validateNew() {
        underTest.validateNew(List.of(validNewItem()));
    }

    @Test
    void validateModel_contentNull() {
        ChecklistItemModel model = ChecklistItemModel.builder()
            .content(null)
            .checked(CHECKED)
            .index(INDEX)
            .type(ItemType.NEW)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "item.content", "must not be null");
    }

    @Test
    void validateModel_checkedNull() {
        ChecklistItemModel model = ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(null)
            .index(INDEX)
            .type(ItemType.NEW)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "item.checked", "must not be null");
    }

    @Test
    void validateModel_indexNull() {
        ChecklistItemModel model = ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(CHECKED)
            .index(null)
            .type(ItemType.NEW)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "item.index", "must not be null");
    }

    @Test
    void validateModel_typeNull() {
        ChecklistItemModel model = ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(CHECKED)
            .index(INDEX)
            .type(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "item.type", "must not be null");
    }

    @Test
    void validateModel_typeExisting_checklistItemIdNull() {
        ChecklistItemModel model = ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(CHECKED)
            .index(INDEX)
            .type(ItemType.EXISTING)
            .checklistItemId(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "item.checklistItemId", "must not be null");
    }

    @Test
    void validateModel_typeExisting() {
        ChecklistItemModel model = ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(CHECKED)
            .index(INDEX)
            .type(ItemType.EXISTING)
            .checklistItemId(CHECKLIST_ITEM_ID)
            .build();

        underTest.validate(model);
    }

    @Test
    void validateModel_typeNew() {
        ChecklistItemModel model = ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(CHECKED)
            .index(INDEX)
            .type(ItemType.NEW)
            .build();

        underTest.validate(model);
    }

    @Test
    void validate_editRequest_itemContentNull() {
        ChecklistItemModel item = ChecklistItemModel.builder()
            .content(null)
            .checked(CHECKED)
            .index(INDEX)
            .type(ItemType.NEW)
            .build();

        EditChecklistRequest request = EditChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(item))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "item.content", "must not be null");
    }

    @Test
    void validate_editRequest_itemCheckedNull() {
        ChecklistItemModel item = ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(null)
            .index(INDEX)
            .type(ItemType.NEW)
            .build();

        EditChecklistRequest request = EditChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(item))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "item.checked", "must not be null");
    }

    @Test
    void validate_editRequest_itemIndexNull() {
        ChecklistItemModel item = ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(CHECKED)
            .index(null)
            .type(ItemType.NEW)
            .build();

        EditChecklistRequest request = EditChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(item))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "item.index", "must not be null");
    }

    @Test
    void validate_editRequest_itemTypeNull() {
        ChecklistItemModel item = ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(CHECKED)
            .index(INDEX)
            .type(null)
            .build();

        EditChecklistRequest request = EditChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(item))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "item.type", "must not be null");
    }

    @Test
    void validate_editRequest_itemChecklistItemIdNull() {
        ChecklistItemModel item = ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(CHECKED)
            .index(INDEX)
            .type(ItemType.EXISTING)
            .checklistItemId(null)
            .build();

        EditChecklistRequest request = EditChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(item))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "item.checklistItemId", "must not be null");
    }

    @Test
    void validate_editRequest_itemsNull() {
        EditChecklistRequest request = EditChecklistRequest.builder()
            .title(TITLE)
            .items(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "items", "must not be null");
    }

    @Test
    void validate_editRequest() {
        ChecklistItemModel item = ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(CHECKED)
            .index(INDEX)
            .type(ItemType.EXISTING)
            .checklistItemId(CHECKLIST_ITEM_ID)
            .build();

        EditChecklistRequest request = EditChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(item))
            .build();

        underTest.validate(request);

        then(titleValidator).should().validate(TITLE);
    }

    private ChecklistItemModel validNewItem() {
        return ChecklistItemModel.builder()
            .content(CONTENT)
            .checked(CHECKED)
            .index(INDEX)
            .build();
    }
}