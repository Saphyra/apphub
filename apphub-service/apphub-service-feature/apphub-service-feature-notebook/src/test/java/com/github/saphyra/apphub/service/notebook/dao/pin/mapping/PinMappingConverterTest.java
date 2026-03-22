package com.github.saphyra.apphub.service.notebook.dao.pin.mapping;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PinMappingConverterTest {
    private static final UUID PIN_MAPPING_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String PIN_MAPPING_ID_STRING = "pin-mapping-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String PIN_GROUP_ID_STRING = "pin-group-id";
    private static final String LIST_ITEM_ID_STRING = "list-item-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private PinMappingConverter underTest;

    @Test
    void convertDomain() {
        PinMapping pinMapping = PinMapping.builder()
            .pinMappingId(PIN_MAPPING_ID)
            .userId(USER_ID)
            .pinGroupId(PIN_GROUP_ID)
            .listItemId(LIST_ITEM_ID)
            .build();

        given(uuidConverter.convertDomain(PIN_MAPPING_ID)).willReturn(PIN_MAPPING_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PIN_GROUP_ID)).willReturn(PIN_GROUP_ID_STRING);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);

        PinMappingEntity result = underTest.convertDomain(pinMapping);

        assertThat(result)
            .returns(PIN_MAPPING_ID_STRING, PinMappingEntity::getPinMappingId)
            .returns(USER_ID_STRING, PinMappingEntity::getUserId)
            .returns(PIN_GROUP_ID_STRING, PinMappingEntity::getPinGroupId)
            .returns(LIST_ITEM_ID_STRING, PinMappingEntity::getListItemId);
    }

    @Test
    void convertEntity() {
        PinMappingEntity entity = PinMappingEntity.builder()
            .pinMappingId(PIN_MAPPING_ID_STRING)
            .userId(USER_ID_STRING)
            .pinGroupId(PIN_GROUP_ID_STRING)
            .listItemId(LIST_ITEM_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(PIN_MAPPING_ID_STRING)).willReturn(PIN_MAPPING_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(PIN_GROUP_ID_STRING)).willReturn(PIN_GROUP_ID);
        given(uuidConverter.convertEntity(LIST_ITEM_ID_STRING)).willReturn(LIST_ITEM_ID);

        PinMapping result = underTest.convertEntity(entity);

        assertThat(result)
            .returns(PIN_MAPPING_ID, PinMapping::getPinMappingId)
            .returns(USER_ID, PinMapping::getUserId)
            .returns(PIN_GROUP_ID, PinMapping::getPinGroupId)
            .returns(LIST_ITEM_ID, PinMapping::getListItemId);
    }
}