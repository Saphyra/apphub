package com.github.saphyra.apphub.service.notebook.dao.list_item.list_item;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_ARCHIVED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_DATA;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PARENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PINNED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_TITLE;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_TYPE;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_USER;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ListItemMapper extends ConverterBase<Map<String, AttributeValue>, ListItemEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(ListItemEntity entity) {
        Map<String, AttributeValue> result = new HashMap<>();

        result.put(COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + entity.getUserId()).build());
        result.put(COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + entity.getListItemId()).build());
        result.put(COLUMN_TYPE, AttributeValue.builder().s(entity.getType()).build());
        result.put(COLUMN_TITLE, AttributeValue.builder().s(entity.getTitle()).build());
        result.put(COLUMN_PINNED, AttributeValue.builder().s(entity.getPinned()).build());
        result.put(COLUMN_ARCHIVED, AttributeValue.builder().s(entity.getArchived()).build());
        result.put(COLUMN_DATA, AttributeValue.builder().s(entity.getData()).build());

        String parentValue = Optional.ofNullable(entity.getParent())
            .orElse("");

        result.put(COLUMN_PARENT, AttributeValue.builder().s(PREFIX_LIST_ITEM + parentValue).build());

        return result;
    }

    @Override
    protected ListItemEntity processEntityConversion(Map<String, AttributeValue> entity) {
        return ListItemEntity.builder()
            .userId(entity.get(COLUMN_PK).s().substring(PREFIX_USER.length()))
            .listItemId(entity.get(COLUMN_SK).s().substring(PREFIX_LIST_ITEM.length()))
            .parent(Optional.ofNullable(entity.get(COLUMN_PARENT)).map(AttributeValue::s).map(s -> s.substring(PREFIX_LIST_ITEM.length())).filter(parent -> !isBlank(parent)).orElse(null))
            .type(entity.get(COLUMN_TYPE).s())
            .title(entity.get(COLUMN_TITLE).s())
            .pinned(entity.get(COLUMN_PINNED).s())
            .archived(entity.get(COLUMN_ARCHIVED).s())
            .data(entity.get(COLUMN_DATA).s())
            .build();
    }
}
