package com.github.saphyra.apphub.service.notebook.service.pin;

import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroup;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDao;
import com.github.saphyra.apphub.service.notebook.service.NotebookViewFactory;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class PinService {
    private final ListItemDao listItemDao;
    private final NotebookViewFactory notebookViewFactory;
    private final PinGroupDao pinGroupDao;

    public void pinListItem(UUID userId, UUID listItemId, Boolean pinned) {
        ValidationUtil.notNull(pinned, "pinned");

        ListItem listItem = listItemDao.findByIdValidated(userId, listItemId);

        listItem.setPinned(pinned);

        listItemDao.save(listItem);
    }

    public List<NotebookView> getPinnedItems(UUID userId, @Nullable UUID pinGroupId) {
        Set<UUID> groupMembers = Optional.ofNullable(pinGroupId)
            .map(_ -> pinGroupDao.findByIdValidated(userId, pinGroupId))
            .map(PinGroup::getListItemIds)
            .orElse(Set.of());

        List<ListItem> listItems = listItemDao.getByUserId(userId)
            .stream()
            .filter(ListItem::isPinned)
            .filter(listItem -> isNull(pinGroupId) || groupMembers.contains(listItem.getListItemId()))
            .collect(Collectors.toList());
        return notebookViewFactory.create(listItems);
    }
}
