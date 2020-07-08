package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.encryption.impl.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
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

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @InjectMocks
    private ListItemConverter underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void convertEntity() {
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
        given(stringEncryptor.decryptEntity(ENCRYPTED_TITLE, ACCESS_TOKEN_USER_ID_STRING)).willReturn(DECRYPTED_TITLE);

        ListItem result = underTest.convertEntity(entity);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getType()).isEqualTo(ListItemType.CHECKLIST);
        assertThat(result.getTitle()).isEqualTo(DECRYPTED_TITLE);
    }

    @Test
    public void convertDomain() {
        ListItem domain = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .type(ListItemType.CHECKLIST)
            .title(DECRYPTED_TITLE)
            .build();

        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(accessTokenProvider.get()).willReturn(accessTokenHeader);
        given(accessTokenHeader.getUserId()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertDomain(ACCESS_TOKEN_USER_ID)).willReturn(ACCESS_TOKEN_USER_ID_STRING);
        given(stringEncryptor.encryptEntity(DECRYPTED_TITLE, ACCESS_TOKEN_USER_ID_STRING)).willReturn(ENCRYPTED_TITLE);

        ListItemEntity result = underTest.convertDomain(domain);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getParent()).isEqualTo(PARENT_STRING);
        assertThat(result.getType()).isEqualTo(ListItemType.CHECKLIST);
        assertThat(result.getTitle()).isEqualTo(ENCRYPTED_TITLE);
    }
}