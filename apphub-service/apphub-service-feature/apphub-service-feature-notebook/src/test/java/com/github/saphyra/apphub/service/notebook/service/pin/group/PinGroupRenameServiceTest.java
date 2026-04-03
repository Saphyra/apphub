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
class PinGroupRenameServiceTest {
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final String NEW_NAME = "new-name";

    @Mock
    private PinGroupNameValidator pinGroupNameValidator;

    @Mock
    private PinGroupDao pinGroupDao;

    @InjectMocks
    private PinGroupRenameService underTest;

    @Mock
    private PinGroup pinGroup;

    @Test
    void rename() {
        given(pinGroupDao.findByIdValidated(PIN_GROUP_ID)).willReturn(pinGroup);

        underTest.rename(PIN_GROUP_ID, NEW_NAME);

        then(pinGroupNameValidator).should().validate(NEW_NAME);
        then(pinGroup).should().setPinGroupName(NEW_NAME);
        then(pinGroupDao).should().save(pinGroup);
    }
}