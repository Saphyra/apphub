package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroup;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PinGroupUpdateService {
    private final PinGroupDao pinGroupDao;
    private final DateTimeUtil dateTimeUtil;

    public void setLastOpened(UUID userId, UUID pinGroupId) {
        PinGroup pinGroup = pinGroupDao.findByIdValidated(userId, pinGroupId);
        pinGroup.setLastOpened(dateTimeUtil.getCurrentDateTime());
        pinGroupDao.save(pinGroup);
    }
}
