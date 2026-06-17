package com.github.saphyra.apphub.service.notebook.dao.pin_group;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDaoConstants.COLUMN_LAST_OPENED;
import static com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDaoConstants.COLUMN_LIST_ITEM_IDS;
import static com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDaoConstants.COLUMN_PIN_GROUP_NAME;
import static com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDaoConstants.PREFIX_PIN_GROUP;
import static com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDaoConstants.PREFIX_USER;

@Component
class PinGroupMapper extends ConverterBase<Map<String, AttributeValue>, PinGroupEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(PinGroupEntity domain) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + domain.getUserId()).build());
        item.put(COLUMN_SK, AttributeValue.builder().s(PREFIX_PIN_GROUP + domain.getPinGroupId()).build());
        item.put(COLUMN_PIN_GROUP_NAME, AttributeValue.builder().s(domain.getPinGroupName()).build());
        item.put(COLUMN_LAST_OPENED, AttributeValue.builder().s(String.valueOf(domain.getLastOpened())).build());
        item.put(COLUMN_LIST_ITEM_IDS, AttributeValue.builder().s(domain.getListItemIds()).build());

        return item;
    }

    @Override
    protected PinGroupEntity processEntityConversion(Map<String, AttributeValue> entity) {
        return PinGroupEntity.builder()
            .userId(entity.get(COLUMN_PK).s().substring(PREFIX_USER.length()))
            .pinGroupId(entity.get(COLUMN_SK).s().substring(PREFIX_PIN_GROUP.length()))
            .pinGroupName(entity.get(COLUMN_PIN_GROUP_NAME).s())
            .lastOpened(Long.parseLong(entity.get(COLUMN_LAST_OPENED).s()))
            .listItemIds(entity.get(COLUMN_LIST_ITEM_IDS).s())
            .build();
    }
}
