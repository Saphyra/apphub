package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotebookViewFactory {
    private final UuidConverter uuidConverter;
    private final StorageProxy storageProxy;
    private final IsParentArchivedService isParentArchivedService;

    public NotebookView create(ListItem listItem) {
        String value = extractValue(listItem);

        UUID parentId = listItem.getParent();

        return NotebookView.builder()
            .id(listItem.getListItemId())
            .title(listItem.getTitle())
            .type(listItem.getType().name())
            .value(value)
            .pinned(listItem.isPinned())
            .archived(listItem.isArchived() || isParentArchivedService.isAnyOfParentsArchived(listItem.getUserId(), listItem.getParent()))
            .parentId(parentId)
            .enabled(fetchEnabled(listItem))
            .build();
    }

    private boolean fetchEnabled(ListItem listItem) {
        switch (listItem.getType()) {
            case FILE, IMAGE -> {
                return storageProxy.getFileMetadata(uuidConverter.convertEntity(listItem.getData()))
                    .getFileUploaded();
            }
            default -> {
                return true;
            }
        }
    }

    private String extractValue(ListItem listItem) {
        switch (listItem.getType()) {
            case LINK -> {
                return listItem.getData();
            }
            case IMAGE, FILE -> {
                return uuidConverter.convertDomain(uuidConverter.convertEntity(listItem.getData()));
            }
            default -> {
                log.debug("No value for listItemType {}", listItem.getType());
                return null;
            }
        }
    }
}
