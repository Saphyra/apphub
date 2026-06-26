package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroup;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PinGroupUpdateServiceTest {
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private PinGroupDao pinGroupDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private PinGroupUpdateService underTest;

    @Mock
    private PinGroup pinGroup;

    @Test
    void setLastOpened() {
        given(pinGroupDao.findByIdValidated(USER_ID, PIN_GROUP_ID)).willReturn(pinGroup);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        underTest.setLastOpened(USER_ID, PIN_GROUP_ID);

        then(pinGroup).should().setLastOpened(CURRENT_TIME);
        then(pinGroupDao).should().save(pinGroup);
    }
}