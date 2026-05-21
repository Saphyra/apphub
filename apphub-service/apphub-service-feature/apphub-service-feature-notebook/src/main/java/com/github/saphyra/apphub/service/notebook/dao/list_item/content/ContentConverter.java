package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_CONTENT;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ContentConverter extends ConverterBase<ContentEntity, Content> {
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected ContentEntity processDomainConversion(Content domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String listItemId = uuidConverter.convertDomain(domain.getListItemId());
        String data = objectMapper.writeValueAsString(domain.getContent());

        return ContentEntity.builder()
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .listItemId(listItemId)
            .parentType(domain.getParentType().name())
            .batchIndex(domain.getBatchIndex())
            .content(stringEncryptor.encrypt(data, userId, listItemId, COLUMN_CONTENT))
            .build();
    }

    @Override
    protected Content processEntityConversion(ContentEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();
        String listItemId = entity.getListItemId();
        String decryptedData = stringEncryptor.decrypt(entity.getContent(), userId, listItemId, COLUMN_CONTENT);
        Map<String, String> contentMap = objectMapper.readValue(decryptedData, new TypeReference<>() {
        });

        return Content.builder()
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .listItemId(uuidConverter.convertEntity(listItemId))
            .batchIndex(entity.getBatchIndex())
            .parentType(ParentType.valueOf(entity.getParentType()))
            .content(contentMap)
            .build();
    }
}
