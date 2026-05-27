package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ContentConverterTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String LIST_ITEM_ID_STRING = "list-item-id";
    private static final Integer BATCH_INDEX = 3;
    private static final String USER_ID = "user-id";
    private static final String SERIALIZED_CONTENT = "{\"key\":\"value\"}";
    private static final String ENCRYPTED_CONTENT = "encrypted-content";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ContentConverter underTest;

    @Test
    void convertDomain() {
        Map<UUID, String> contentMap = new HashMap<>();
        contentMap.put(UUID.randomUUID(), "value");

        Content domain = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(BATCH_INDEX)
            .content(contentMap)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(objectMapper.writeValueAsString(contentMap)).willReturn(SERIALIZED_CONTENT);
        given(stringEncryptor.encrypt(SERIALIZED_CONTENT, USER_ID, LIST_ITEM_ID_STRING, COLUMN_CONTENT)).willReturn(ENCRYPTED_CONTENT);

        ContentEntity result = underTest.convertDomain(domain);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID_STRING);
        assertThat(result.getBatchIndex()).isEqualTo(BATCH_INDEX);
        assertThat(result.getContent()).isEqualTo(ENCRYPTED_CONTENT);
    }

    @Test
    void convertEntity() {
        Map<UUID, String> contentMap = new HashMap<>();
        contentMap.put(UUID.randomUUID(), "value");

        ContentEntity entity = ContentEntity.builder()
            .listItemId(LIST_ITEM_ID_STRING)
            .batchIndex(BATCH_INDEX)
            .content(ENCRYPTED_CONTENT)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_CONTENT, USER_ID, LIST_ITEM_ID_STRING, COLUMN_CONTENT)).willReturn(SERIALIZED_CONTENT);
        given(objectMapper.readValue(eq(SERIALIZED_CONTENT), any(TypeReference.class))).willReturn(contentMap);
        given(uuidConverter.convertEntity(LIST_ITEM_ID_STRING)).willReturn(LIST_ITEM_ID);

        Content result = underTest.convertEntity(entity);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getBatchIndex()).isEqualTo(BATCH_INDEX);
        assertThat(result.getContent()).isEqualTo(contentMap);
        assertThat(result.isModified()).isFalse();
    }
}