package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotebookViewFactory {
    private final ContentDao contentDao;
    private final ListItemDao listItemDao;
    private final FileDao fileDao;
    private final UuidConverter uuidConverter;
    private final StorageProxy storageProxy;
    private final IsParentArchivedService isParentArchivedService;

    public NotebookView create(ListItem listItem) {
        String value = extractValue(listItem);

        UUID parentId = listItem.getParent();
        String parentTitle = Optional.ofNullable(parentId)
            .map(listItemDao::findByIdValidated)
            .map(ListItem::getTitle)
            .orElse(null);

        return NotebookView.builder()
            .id(listItem.getListItemId())
            .title(listItem.getTitle())
            .type(listItem.getType().name())
            .value(value)
            .pinned(listItem.isPinned())
            .archived(listItem.isArchived() || isParentArchivedService.isAnyOfParentsArchived(listItem.getParent()))
            .parentId(parentId)
            .parentTitle(parentTitle)
            .enabled(fetchEnabled(listItem))
            .build();
    }

    private boolean fetchEnabled(ListItem listItem) {
        switch (listItem.getType()) {
            case FILE, IMAGE -> {
                UUID storedFileId = fileDao.findByParentValidated(listItem.getListItemId())
                    .getStoredFileId();
                return storageProxy.getFileMetadata(storedFileId)
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
                return contentDao.findByParentValidated(listItem.getListItemId())
                    .getContent();
            }
            case IMAGE, FILE -> {
                UUID fileId = fileDao.findByParentValidated(listItem.getListItemId())
                    .getStoredFileId();
                return uuidConverter.convertDomain(fileId);
            }
            default -> {
                log.debug("No value for listItemType {}", listItem.getType());
                return null;
            }
        }
    }
}
