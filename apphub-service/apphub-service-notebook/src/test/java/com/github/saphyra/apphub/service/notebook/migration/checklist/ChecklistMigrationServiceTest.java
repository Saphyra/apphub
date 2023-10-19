package com.github.saphyra.apphub.service.notebook.migration.checklist;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.migration.table.UnencryptedListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistMigrationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final Integer ORDER = 42;

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private DimensionFactory dimensionFactory;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private CheckedItemFactory checkedItemFactory;

    @Mock
    private CheckedItemDao checkedItemDao;

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private ChecklistMigrationService underTest;

    @Mock
    private UnencryptedListItem checklist;

    @Mock
    private UnencryptedListItem other;

    @Mock
    private ChecklistItem checklistItem;

    @Mock
    private Dimension dimension;

    @Mock
    private CheckedItem checkedItem;

    @Test
    void migrate() {
        given(listItemDao.getAllUnencrypted()).willReturn(List.of(checklist, checklist, other));
        given(checklist.getUserId()).willReturn(USER_ID);
        given(checklist.getType()).willReturn(ListItemType.CHECKLIST);
        given(other.getType()).willReturn(ListItemType.CHECKLIST_TABLE);

        given(checklistItemDao.getByUserId(USER_ID)).willReturn(List.of(checklistItem));
        given(checklistItem.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);
        given(checklistItem.getUserId()).willReturn(USER_ID);
        given(checklistItem.getParent()).willReturn(LIST_ITEM_ID);
        given(checklistItem.getOrder()).willReturn(ORDER);
        given(checklistItem.getChecked()).willReturn(true);
        given(dimensionFactory.create(USER_ID, LIST_ITEM_ID, ORDER, CHECKLIST_ITEM_ID)).willReturn(dimension);
        given(dimension.getDimensionId()).willReturn(CHECKLIST_ITEM_ID);
        given(checkedItemFactory.create(USER_ID, CHECKLIST_ITEM_ID, true)).willReturn(checkedItem);

        underTest.migrate();

        ArgumentCaptor<AccessTokenHeader> argumentCaptor = ArgumentCaptor.forClass(AccessTokenHeader.class);
        then(accessTokenProvider).should().set(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getUserId()).isEqualTo(USER_ID);

        then(dimensionDao).should().save(dimension);
        then(checkedItemDao).should().save(checkedItem);

        then(accessTokenProvider).should().clear();
    }
}