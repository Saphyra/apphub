package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_CHECKED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_INDEX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChecklistItemConverterTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final String LIST_ITEM_ID_STRING = "list-item-id";
    private static final String CHECKLIST_ITEM_ID_STRING = "checklist-item-id";
    private static final String USER_ID = "user-id";
    private static final boolean CHECKED = true;
    private static final String CHECKED_ENCRYPTED = "checked-encrypted";
    private static final int INDEX = 3;
    private static final String INDEX_ENCRYPTED = "index-encrypted";

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ChecklistItemConverter underTest;

    @Test
    void convertDomain() {
        ChecklistItem domain = ChecklistItem.builder()
            .listItemId(LIST_ITEM_ID)
            .checklistItemId(CHECKLIST_ITEM_ID)
            .checked(CHECKED)
            .index(INDEX)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(CHECKLIST_ITEM_ID)).willReturn(CHECKLIST_ITEM_ID_STRING);
        given(booleanEncryptor.encrypt(CHECKED, USER_ID, CHECKLIST_ITEM_ID_STRING, COLUMN_CHECKED)).willReturn(CHECKED_ENCRYPTED);
        given(integerEncryptor.encrypt(INDEX, USER_ID, CHECKLIST_ITEM_ID_STRING, COLUMN_INDEX)).willReturn(INDEX_ENCRYPTED);

        ChecklistItemEntity result = underTest.convertDomain(domain);

        assertThat(result)
            .returns(LIST_ITEM_ID_STRING, ChecklistItemEntity::getListItemId)
            .returns(CHECKLIST_ITEM_ID_STRING, ChecklistItemEntity::getChecklistItemId)
            .returns(CHECKED_ENCRYPTED, ChecklistItemEntity::getChecked)
            .returns(INDEX_ENCRYPTED, ChecklistItemEntity::getIndex);
    }

    @Test
    void convertEntity() {
        ChecklistItemEntity entity = ChecklistItemEntity.builder()
            .listItemId(LIST_ITEM_ID_STRING)
            .checklistItemId(CHECKLIST_ITEM_ID_STRING)
            .checked(CHECKED_ENCRYPTED)
            .index(INDEX_ENCRYPTED)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID);
        given(uuidConverter.convertEntity(LIST_ITEM_ID_STRING)).willReturn(LIST_ITEM_ID);
        given(uuidConverter.convertEntity(CHECKLIST_ITEM_ID_STRING)).willReturn(CHECKLIST_ITEM_ID);
        given(booleanEncryptor.decrypt(CHECKED_ENCRYPTED, USER_ID, CHECKLIST_ITEM_ID_STRING, COLUMN_CHECKED)).willReturn(CHECKED);
        given(integerEncryptor.decrypt(INDEX_ENCRYPTED, USER_ID, CHECKLIST_ITEM_ID_STRING, COLUMN_INDEX)).willReturn(INDEX);

        ChecklistItem result = underTest.convertEntity(entity);

        assertThat(result)
            .returns(LIST_ITEM_ID, ChecklistItem::getListItemId)
            .returns(CHECKLIST_ITEM_ID, ChecklistItem::getChecklistItemId)
            .returns(CHECKED, ChecklistItem::isChecked)
            .returns(INDEX, ChecklistItem::getIndex);
    }
}