package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroup;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDao;
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

    public void addItem(UUID userId, UUID pinGroupId, UUID listItemId) {
        listItemDao.findByIdValidated(userId, listItemId); //Validate existing and own
        PinGroup pinGroup = pinGroupDao.findByIdValidated(userId, pinGroupId); //Validate existing and own

        if (pinGroup.getListItemIds().contains(listItemId)) {
            log.debug("List item {} is already in PinGroup {}", listItemId, pinGroupId);
            return;
        }

        pinGroup.addListItem(listItemId);
        pinGroupDao.save(pinGroup);
    }

    public void removeItem(UUID userId, UUID pinGroupId, UUID listItemId) {
        PinGroup pinGroup = pinGroupDao.findByIdValidated(userId, pinGroupId);

        if (!pinGroup.getListItemIds().contains(listItemId)) {
            log.debug("List item {} is not in PinGroup {}", listItemId, pinGroupId);
            return;
        }

        pinGroup.removeListItem(listItemId);
        pinGroupDao.save(pinGroup);
    }
}
