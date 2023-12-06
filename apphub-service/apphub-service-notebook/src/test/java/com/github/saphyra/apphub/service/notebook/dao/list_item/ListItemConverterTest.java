package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemConverter.COLUMN_ARCHIVED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemConverter.COLUMN_PINNED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemConverter.COLUMN_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ListItemConverterTest {
    private static final String LIST_ITEM_ID_STRING = "list-item-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String PARENT_STRING = "parent";
    private static final String ENCRYPTED_TITLE = "encrypted-title";
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID ACCESS_TOKEN_USER_ID = UUID.randomUUID();
    private static final String ACCESS_TOKEN_USER_ID_STRING = "access-token-user-id-string";
    private static final String DECRYPTED_TITLE = "decrypted-title";
    private static final String ENCRYPTED_PINNED = "encrypyted-pinned";
    private static final String ENCRYPTED_ARCHIVED = "encrypted-archived";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @InjectMocks
    private ListItemConverter underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void convertEntity_nullBooleans() {
        ListItemEntity entity = ListItemEntity.builder()
            .listItemId(LIST_ITEM_ID_STRING)
            .userId(USER_ID_STRING)
            .parent(PARENT_STRING)
            .type(ListItemType.CHECKLIST)
            .title(ENCRYPTED_TITLE)
            .build();

        given(uuidConverter.convertEntity(LIST_ITEM_ID_STRING)).willReturn(LIST_ITEM_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(PARENT_STRING)).willReturn(PARENT);
        given(accessTokenProvider.get()).willReturn(accessTokenHeader);
        given(accessTokenHeader.getUserId()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertDomain(ACCESS_TOKEN_USER_ID)).willReturn(ACCESS_TOKEN_USER_ID_STRING);
        given(stringEncryptor.decrypt(ENCRYPTED_TITLE, ACCESS_TOKEN_USER_ID_STRING, LIST_ITEM_ID_STRING, COLUMN_TITLE)).willReturn(DECRYPTED_TITLE);

        ListItem result = underTest.convertEntity(entity);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getType()).isEqualTo(ListItemType.CHECKLIST);
        assertThat(result.getTitle()).isEqualTo(DECRYPTED_TITLE);
        assertThat(result.isPinned()).isFalse();
        assertThat(result.isArchived()).isFalse();
    }

    @Test
    public void convertEntity() {
        ListItemEntity entity = ListItemEntity.builder()
            .listItemId(LIST_ITEM_ID_STRING)
            .userId(USER_ID_STRING)
            .parent(PARENT_STRING)
            .type(ListItemType.CHECKLIST)
            .title(ENCRYPTED_TITLE)
            .pinned(ENCRYPTED_PINNED)
            .archived(ENCRYPTED_ARCHIVED)
            .build();

        given(uuidConverter.convertEntity(LIST_ITEM_ID_STRING)).willReturn(LIST_ITEM_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(PARENT_STRING)).willReturn(PARENT);
        given(accessTokenProvider.get()).willReturn(accessTokenHeader);
        given(accessTokenHeader.getUserId()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertDomain(ACCESS_TOKEN_USER_ID)).willReturn(ACCESS_TOKEN_USER_ID_STRING);
        given(stringEncryptor.decrypt(ENCRYPTED_TITLE, ACCESS_TOKEN_USER_ID_STRING, LIST_ITEM_ID_STRING, COLUMN_TITLE)).willReturn(DECRYPTED_TITLE);
        given(booleanEncryptor.decrypt(ENCRYPTED_PINNED, ACCESS_TOKEN_USER_ID_STRING, LIST_ITEM_ID_STRING, COLUMN_PINNED)).willReturn(true);
        given(booleanEncryptor.decrypt(ENCRYPTED_ARCHIVED, ACCESS_TOKEN_USER_ID_STRING, LIST_ITEM_ID_STRING, COLUMN_ARCHIVED)).willReturn(true);

        ListItem result = underTest.convertEntity(entity);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getType()).isEqualTo(ListItemType.CHECKLIST);
        assertThat(result.getTitle()).isEqualTo(DECRYPTED_TITLE);
        assertThat(result.isPinned()).isTrue();
        assertThat(result.isArchived()).isTrue();
    }

    @Test
    public void convertDomain() {
        ListItem domain = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .type(ListItemType.CHECKLIST)
            .title(DECRYPTED_TITLE)
            .pinned(true)
            .archived(true)
            .build();

        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(accessTokenProvider.get()).willReturn(accessTokenHeader);
        given(accessTokenHeader.getUserId()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertDomain(ACCESS_TOKEN_USER_ID)).willReturn(ACCESS_TOKEN_USER_ID_STRING);
        given(stringEncryptor.encrypt(DECRYPTED_TITLE, ACCESS_TOKEN_USER_ID_STRING, LIST_ITEM_ID_STRING, COLUMN_TITLE)).willReturn(ENCRYPTED_TITLE);
        given(booleanEncryptor.encrypt(true, ACCESS_TOKEN_USER_ID_STRING, LIST_ITEM_ID_STRING, COLUMN_PINNED)).willReturn(ENCRYPTED_PINNED);
        given(booleanEncryptor.encrypt(true, ACCESS_TOKEN_USER_ID_STRING, LIST_ITEM_ID_STRING, COLUMN_ARCHIVED)).willReturn(ENCRYPTED_ARCHIVED);

        ListItemEntity result = underTest.convertDomain(domain);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getParent()).isEqualTo(PARENT_STRING);
        assertThat(result.getType()).isEqualTo(ListItemType.CHECKLIST);
        assertThat(result.getTitle()).isEqualTo(ENCRYPTED_TITLE);
        assertThat(result.getPinned()).isEqualTo(ENCRYPTED_PINNED);
        assertThat(result.getArchived()).isEqualTo(ENCRYPTED_ARCHIVED);
    }
}