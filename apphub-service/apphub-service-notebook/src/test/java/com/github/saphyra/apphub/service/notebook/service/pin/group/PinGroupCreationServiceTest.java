package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroup;
import com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroupDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PinGroupCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PIN_GROUP_NAME = "pin-group-name";

    @Mock
    private PinGroupNameValidator pinGroupNameValidator;

    @Mock
    private PinGroupFactory pinGroupFactory;

    @Mock
    private PinGroupDao pinGroupDao;

    @InjectMocks
    private PinGroupCreationService underTest;

    @Mock
    private PinGroup pinGroup;

    @Test
    void create() {
        given(pinGroupFactory.create(USER_ID, PIN_GROUP_NAME)).willReturn(pinGroup);

        underTest.create(USER_ID, PIN_GROUP_NAME);

        then(pinGroupNameValidator).should().validate(PIN_GROUP_NAME);
        then(pinGroupDao).should().save(pinGroup);
    }
}