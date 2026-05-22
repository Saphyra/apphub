package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_CONTENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_CONTENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;

@Component
@Slf4j
//TODO unit test
class ContentMapper extends ConverterBase<Map<String, AttributeValue>, ContentEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(ContentEntity domain) {
        return Map.of(
            COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + domain.getListItemId()).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_CONTENT + domain.getBatchIndex()).build(),
            COLUMN_CONTENT, AttributeValue.builder().s(domain.getContent()).build()
        );
    }

    @Override
    protected ContentEntity processEntityConversion(Map<String, AttributeValue> entity) {
        return ContentEntity.builder()
            .listItemId(entity.get(COLUMN_PK).s().substring(PREFIX_LIST_ITEM.length()))
            .batchIndex(Integer.parseInt( entity.get(COLUMN_SK).s().substring(PREFIX_CONTENT.length())))
            .content(entity.get(COLUMN_CONTENT).s())
            .build();
    }
}
