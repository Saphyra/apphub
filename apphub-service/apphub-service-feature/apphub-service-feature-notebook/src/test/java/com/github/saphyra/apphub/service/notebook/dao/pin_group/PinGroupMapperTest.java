package com.github.saphyra.apphub.service.notebook.dao.pin_group;

import org.junit.jupiter.api.Test;
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
import static org.assertj.core.api.Assertions.assertThat;

class PinGroupMapperTest {
    private static final String USER_ID = "user-id";
    private static final String PIN_GROUP_ID = "pin-group-id";
    private static final String PIN_GROUP_NAME = "encrypted-pin-group-name";
    private static final Long LAST_OPENED = 123L;
    private static final String LIST_ITEM_IDS = "encrypted-list-item-ids";

    private final PinGroupMapper underTest = new PinGroupMapper();

    @Test
    void convertDomain() {
        PinGroupEntity domain = PinGroupEntity.builder()
            .userId(USER_ID)
            .pinGroupId(PIN_GROUP_ID)
            .pinGroupName(PIN_GROUP_NAME)
            .lastOpened(LAST_OPENED)
            .listItemIds(LIST_ITEM_IDS)
            .build();

        Map<String, AttributeValue> result = underTest.convertDomain(domain);

        assertThat(result.get(COLUMN_PK).s()).isEqualTo(PREFIX_USER + USER_ID);
        assertThat(result.get(COLUMN_SK).s()).isEqualTo(PREFIX_PIN_GROUP + PIN_GROUP_ID);
        assertThat(result.get(COLUMN_PIN_GROUP_NAME).s()).isEqualTo(PIN_GROUP_NAME);
        assertThat(result.get(COLUMN_LAST_OPENED).s()).isEqualTo(String.valueOf(LAST_OPENED));
        assertThat(result.get(COLUMN_LIST_ITEM_IDS).s()).isEqualTo(LIST_ITEM_IDS);
    }

    @Test
    void convertEntity() {
        Map<String, AttributeValue> entity = new HashMap<>();
        entity.put(COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + USER_ID).build());
        entity.put(COLUMN_SK, AttributeValue.builder().s(PREFIX_PIN_GROUP + PIN_GROUP_ID).build());
        entity.put(COLUMN_PIN_GROUP_NAME, AttributeValue.builder().s(PIN_GROUP_NAME).build());
        entity.put(COLUMN_LAST_OPENED, AttributeValue.builder().s(String.valueOf(LAST_OPENED)).build());
        entity.put(COLUMN_LIST_ITEM_IDS, AttributeValue.builder().s(LIST_ITEM_IDS).build());

        PinGroupEntity result = underTest.convertEntity(entity);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getPinGroupId()).isEqualTo(PIN_GROUP_ID);
        assertThat(result.getPinGroupName()).isEqualTo(PIN_GROUP_NAME);
        assertThat(result.getLastOpened()).isEqualTo(LAST_OPENED);
        assertThat(result.getListItemIds()).isEqualTo(LIST_ITEM_IDS);
    }
}