package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroup;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PinGroupRenameService {
    private final PinGroupNameValidator pinGroupNameValidator;
    private final PinGroupDao pinGroupDao;

    public void rename(UUID userId, UUID pinGroupId, String newName) {
        pinGroupNameValidator.validate(newName);

        PinGroup deprecatedPinGroup = pinGroupDao.findByIdValidated(userId, pinGroupId);
        deprecatedPinGroup.setPinGroupName(newName);
        pinGroupDao.save(deprecatedPinGroup);
    }
}
