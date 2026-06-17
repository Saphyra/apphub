package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroup;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDao;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PinGroupCreationService {
    private final PinGroupNameValidator pinGroupNameValidator;
    private final PinGroupFactory pinGroupFactory;
    private final PinGroupDao pinGroupDao;

    public void create(UUID userId, String pinGroupName) {
        pinGroupNameValidator.validate(pinGroupName);

        PinGroup pinGroup = pinGroupFactory.create(userId, pinGroupName);

        pinGroupDao.save(pinGroup);
    }
}
