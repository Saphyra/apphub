package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroup;
import com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroupDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PinGroupRenameService {
    private final PinGroupNameValidator pinGroupNameValidator;
    private final PinGroupDao pinGroupDao;

    public void rename(UUID pinGroupId, String newName) {
        pinGroupNameValidator.validate(newName);

        PinGroup pinGroup = pinGroupDao.findByIdValidated(pinGroupId);
        pinGroup.setPinGroupName(newName);
        pinGroupDao.save(pinGroup);
    }
}
