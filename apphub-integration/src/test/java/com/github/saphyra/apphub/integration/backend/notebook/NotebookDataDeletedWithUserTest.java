package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ChecklistActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.PinActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TableActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.notebook.NotebookListItemDynamoDbRepository;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.notebook.NotebookPinGroupDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableColumnModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableHeadModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableRowModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class NotebookDataDeletedWithUserTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String PIN_GROUP = "pin-group";

    @Test(groups = {"be", "notebook"})
    public void notebookDataDeletedWithUser() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);
        UUID userId = UserDynamoDbRepository.getUserIdByEmail(userData.getEmail());

        UUID tableId = createTable(accessToken);
        UUID checklistId = createChecklist(accessToken);

        PinActions.createPinGroup(getServerPort(), accessToken, PIN_GROUP);

        AccountActions.deleteAccount(getServerPort(), accessToken, userData.getPassword());

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(NotebookListItemDynamoDbRepository.listItemExists(userId)).isFalse();
            assertThat(NotebookListItemDynamoDbRepository.listItemHasChildren(tableId)).isFalse();
            assertThat(NotebookListItemDynamoDbRepository.listItemHasChildren(checklistId)).isFalse();
            assertThat(NotebookPinGroupDynamoDbRepository.getPinGroups(userId)).isEmpty();
        });
    }

    private static UUID createChecklist(String accessToken) {
        CreateChecklistRequest createChecklistRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(ChecklistItemModel.builder()
                .index(0)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        return ChecklistActions.createChecklist(getServerPort(), accessToken, createChecklistRequest);
    }

    private static UUID createTable(String accessToken) {
        CreateTableRequest createTableRequest = CreateTableRequest.builder()
            .title(TITLE)
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(CONTENT)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(CONTENT)
                    .build()))
                .build()))
            .build();
        TableActions.createTable(getServerPort(), accessToken, createTableRequest);
        return CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, null)
            .getChildren()
            .getFirst()
            .getId();
    }
}
