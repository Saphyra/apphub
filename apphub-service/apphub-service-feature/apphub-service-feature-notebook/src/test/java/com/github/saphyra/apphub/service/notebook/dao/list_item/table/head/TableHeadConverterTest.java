package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

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

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableHeadConverterTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String LIST_ITEM_ID_STRING = "list-item-id";
    private static final String USER_ID = "user-id";
    private static final String SERIALIZED_DATA = "[{...}]";
    private static final String ENCRYPTED_DATA = "encrypted-data";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TableHeadConverter underTest;

    @Mock
    private TableHead tableHead;

    @Test
    void convertDomain() {
        List<TableHead> tableHeads = List.of(tableHead);

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID);
        given(objectMapper.writeValueAsString(tableHeads)).willReturn(SERIALIZED_DATA);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(stringEncryptor.encrypt(SERIALIZED_DATA, USER_ID, LIST_ITEM_ID_STRING, COLUMN_DATA)).willReturn(ENCRYPTED_DATA);

        TableHeadEntity result = underTest.convertDomain(LIST_ITEM_ID, tableHeads);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID_STRING);
        assertThat(result.getData()).isEqualTo(ENCRYPTED_DATA);
    }

    @Test
    void convertEntity() {
        List<TableHead> tableHeads = List.of(tableHead);
        TableHeadEntity entity = TableHeadEntity.builder()
            .listItemId(LIST_ITEM_ID_STRING)
            .data(ENCRYPTED_DATA)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_DATA, USER_ID, LIST_ITEM_ID_STRING, COLUMN_DATA)).willReturn(SERIALIZED_DATA);
        given(objectMapper.readValue(eq(SERIALIZED_DATA), any(TypeReference.class))).willReturn(tableHeads);

        List<TableHead> result = underTest.convertEntity(entity);

        assertThat(result).isEqualTo(tableHeads);
    }
}