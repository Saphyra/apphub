package com.github.saphyra.apphub.service.notebook.dao.content;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.notebook.dao.content.ContentConverter.COLUMN_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ContentConverterTest {
    private static final String CONTENT_ID_STRING = "content-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String PARENT_STRING = "parent";
    private static final String ENCRYPTED_CONTENT = "encrypted-content";
    private static final UUID CONTENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID ACCESS_TOKEN_USER_ID = UUID.randomUUID();
    private static final String ACCESS_TOKEN_USER_ID_STRING = "access-token-user-id";
    private static final String DECRYPTED_CONTENT = "decrypted-content";
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String LIST_ITEM_ID_STRING = "list-item-id";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ContentConverter underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @BeforeEach
    public void setUp() {
        given(accessTokenProvider.get()).willReturn(accessTokenHeader);
        given(accessTokenHeader.getUserId()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertDomain(ACCESS_TOKEN_USER_ID)).willReturn(ACCESS_TOKEN_USER_ID_STRING);
    }

    @Test
    public void convertEntity() {
        ContentEntity entity = ContentEntity.builder()
            .contentId(CONTENT_ID_STRING)
            .userId(USER_ID_STRING)
            .parent(PARENT_STRING)
            .listItemId(LIST_ITEM_ID_STRING)
            .content(ENCRYPTED_CONTENT)
            .build();
        given(uuidConverter.convertEntity(CONTENT_ID_STRING)).willReturn(CONTENT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(PARENT_STRING)).willReturn(PARENT);
        given(uuidConverter.convertEntity(LIST_ITEM_ID_STRING)).willReturn(LIST_ITEM_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_CONTENT, ACCESS_TOKEN_USER_ID_STRING, CONTENT_ID_STRING, COLUMN_CONTENT)).willReturn(DECRYPTED_CONTENT);

        Content result = underTest.convertEntity(entity);

        assertThat(result.getContentId()).isEqualTo(CONTENT_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getContent()).isEqualTo(DECRYPTED_CONTENT);
    }

    @Test
    public void convertDomain() {
        Content entity = Content.builder()
            .contentId(CONTENT_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .listItemId(LIST_ITEM_ID)
            .content(DECRYPTED_CONTENT)
            .build();
        given(uuidConverter.convertDomain(CONTENT_ID)).willReturn(CONTENT_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(stringEncryptor.encrypt(DECRYPTED_CONTENT, ACCESS_TOKEN_USER_ID_STRING, CONTENT_ID_STRING, COLUMN_CONTENT)).willReturn(ENCRYPTED_CONTENT);


        ContentEntity result = underTest.convertDomain(entity);

        assertThat(result.getContentId()).isEqualTo(CONTENT_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getParent()).isEqualTo(PARENT_STRING);
        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID_STRING);
        assertThat(result.getContent()).isEqualTo(ENCRYPTED_CONTENT);
    }
}