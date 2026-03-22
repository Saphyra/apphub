package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.pin.mapping.PinMapping;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PinMappingFactoryTest {
    private static final UUID PIN_MAPPING_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private PinMappingFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(PIN_MAPPING_ID);

        assertThat(underTest.create(USER_ID, PIN_GROUP_ID, LIST_ITEM_ID))
            .returns(PIN_MAPPING_ID, PinMapping::getPinMappingId)
            .returns(USER_ID, PinMapping::getUserId)
            .returns(PIN_GROUP_ID, PinMapping::getPinGroupId)
            .returns(LIST_ITEM_ID, PinMapping::getListItemId);
    }
}