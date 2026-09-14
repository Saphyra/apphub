package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotebookViewFactory {
    private final UuidConverter uuidConverter;
    private final StorageProxy storageProxy;
    private final IsParentArchivedService isParentArchivedService;
    private final ExecutorServiceBean executorServiceBean;
    private final AccessTokenProvider accessTokenProvider;

    public List<NotebookView> create(List<ListItem> listItems) {
        if (listItems.isEmpty()) {
            return List.of();
        }

        Map<UUID, ListItem> cache = new ConcurrentHashMap<>();

        return executorServiceBean.processCollectionWithWait(listItems, listItem -> {
            try (var _ = accessTokenProvider.set(AccessToken.builder().userId(listItem.getUserId()).build())) {
                return create(listItem, cache);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private NotebookView create(ListItem listItem, Map<UUID, ListItem> cache) {
        String value = extractValue(listItem);

        UUID parentId = listItem.getParent();

        return NotebookView.builder()
            .id(listItem.getListItemId())
            .title(listItem.getTitle())
            .type(listItem.getType().name())
            .value(value)
            .pinned(listItem.isPinned())
            .archived(listItem.isArchived() || isParentArchivedService.isAnyOfParentsArchived(cache, listItem.getUserId(), listItem.getParent()))
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
            case LINK, IMAGE, FILE -> {
                return listItem.getData();
            }
            default -> {
                log.debug("No value for listItemType {}", listItem.getType());
                return null;
            }
        }
    }
}
