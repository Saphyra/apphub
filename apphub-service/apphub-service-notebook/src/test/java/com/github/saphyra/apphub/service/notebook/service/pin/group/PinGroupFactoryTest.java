package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PinGroupFactoryTest {
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PIN_GROUP_NAME = "pin-group-name";

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private PinGroupFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(PIN_GROUP_ID);

        PinGroup result = underTest.create(USER_ID, PIN_GROUP_NAME);

        assertThat(result)
            .returns(PIN_GROUP_ID, PinGroup::getPinGroupId)
            .returns(USER_ID, PinGroup::getUserId)
            .returns(PIN_GROUP_NAME, PinGroup::getPinGroupName);
    }
}