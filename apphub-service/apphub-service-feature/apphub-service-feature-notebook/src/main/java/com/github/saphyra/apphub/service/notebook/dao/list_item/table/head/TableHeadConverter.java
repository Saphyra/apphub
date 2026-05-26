package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_DATA;

@Component
@RequiredArgsConstructor
class TableHeadConverter {
    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final ObjectMapper objectMapper;

    TableHeadEntity convertDomain(UUID listItemId, List<TableHead> tableHeads) {
        String userIdFromAccessToken = accessTokenProvider.getUserIdAsString();
        String data = objectMapper.writeValueAsString(tableHeads);
        String listItemIdString = uuidConverter.convertDomain(listItemId);
        String encryptedData = stringEncryptor.encrypt(data, userIdFromAccessToken, listItemIdString, COLUMN_DATA);

        return TableHeadEntity.builder()
            .listItemId(listItemIdString)
            .data(encryptedData)
            .build();
    }

    public List<TableHead> convertEntity(TableHeadEntity entity) {
        String decryptedData = stringEncryptor.decrypt(entity.getData(), accessTokenProvider.getUserIdAsString(), entity.getListItemId(), COLUMN_DATA);

        return objectMapper.readValue(decryptedData, new TypeReference<>() {
        });
    }
}
