package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PinGroupDeletionServiceTest {
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private PinGroupDao pinGroupDao;

    @InjectMocks
    private PinGroupDeletionService underTest;

    @Test
    void delete() {
        underTest.delete(USER_ID, PIN_GROUP_ID);

        pinGroupDao.delete(USER_ID, PIN_GROUP_ID);
    }
}