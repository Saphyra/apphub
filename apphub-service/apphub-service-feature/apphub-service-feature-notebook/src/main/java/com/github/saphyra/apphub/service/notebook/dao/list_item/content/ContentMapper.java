package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_CONTENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_BATCH;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_CONTENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_USER;

@Component
@Slf4j
//TODO unit test
class ContentMapper extends ConverterBase<Map<String, AttributeValue>, ContentEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(ContentEntity domain) {
        return Map.of(
            COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + domain.getUserId()).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_CONTENT + domain.getParentType() + "|" + PREFIX_LIST_ITEM + domain.getListItemId() + "|" + PREFIX_BATCH + domain.getBatchIndex()).build(),
            COLUMN_CONTENT, AttributeValue.builder().s(domain.getContent()).build()
        );
    }

    @Override
    protected ContentEntity processEntityConversion(Map<String, AttributeValue> entity) {
        String sk = entity.get(COLUMN_SK).s();

        // sk format: PREFIX_CONTENT + parentType + "|" + PREFIX_LIST_ITEM + listItemId + "|" + PREFIX_BATCH + batchIndex
        String withoutContentPrefix = sk.substring(PREFIX_CONTENT.length());
        int firstPipe = withoutContentPrefix.indexOf("|");
        String parentType = withoutContentPrefix.substring(0, firstPipe);

        String remainder = withoutContentPrefix.substring(firstPipe + 1 + PREFIX_LIST_ITEM.length());
        int secondPipe = remainder.indexOf("|");
        String listItemId = remainder.substring(0, secondPipe);
        int batchIndex = Integer.parseInt(remainder.substring(secondPipe + 1 + PREFIX_BATCH.length()));

        return ContentEntity.builder()
            .userId(entity.get(COLUMN_USER_ID).s().substring(PREFIX_USER.length()))
            .listItemId(listItemId)
            .batchIndex(batchIndex)
            .parentType(parentType)
            .content(entity.get(COLUMN_CONTENT).s())
            .build();
    }
}
