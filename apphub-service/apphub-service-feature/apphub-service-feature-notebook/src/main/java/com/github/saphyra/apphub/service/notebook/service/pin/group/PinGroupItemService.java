package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroupDao;
import com.github.saphyra.apphub.service.notebook.dao.pin.mapping.PinMapping;
import com.github.saphyra.apphub.service.notebook.dao.pin.mapping.PinMappingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PinGroupItemService {
    private final ListItemDao listItemDao;
    private final PinGroupDao pinGroupDao;
    private final PinMappingDao pinMappingDao;
    private final PinMappingFactory pinMappingFactory;

    public void addItem(UUID userId, UUID pinGroupId, UUID listItemId) {
        listItemDao.findByIdValidated(listItemId); //Validate existing and own
        pinGroupDao.findByIdValidated(pinGroupId); //Validate existing and own

        if (pinMappingDao.findByPinGroupIdAndListItemId(pinGroupId, listItemId).isPresent()) {
            log.debug("List item {} is already in PinGroup {}", listItemId, pinGroupId);
            return;
        }

        PinMapping pinMapping = pinMappingFactory.create(userId, pinGroupId, listItemId);
        pinMappingDao.save(pinMapping);
    }

    public void removeItem(UUID userId, UUID pinGroupId, UUID listItemId) {
        PinMapping pinMapping = pinMappingDao.findByPinGroupIdAndListItemIdValidated(pinGroupId, listItemId);

        if (!pinMapping.getUserId().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " has no access to PinMapping " + pinMapping.getPinMappingId());
        }

        pinMappingDao.delete(pinMapping);
    }
}
