package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.migration.table.UnencryptedListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Component
@Slf4j
class ListItemConverter extends ConverterBase<ListItemEntity, ListItem> {
    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final BooleanEncryptor booleanEncryptor;

    @Override
    protected ListItem processEntityConversion(ListItemEntity entity) {
        String userId = uuidConverter.convertDomain(accessTokenProvider.get().getUserId());

        return ListItem.builder()
            .listItemId(uuidConverter.convertEntity(entity.getListItemId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parent(uuidConverter.convertEntity(entity.getParent()))
            .type(entity.getType())
            .title(stringEncryptor.decryptEntity(entity.getTitle(), userId))
            .pinned(Optional.ofNullable(entity.getPinned()).map(s -> booleanEncryptor.decryptEntity(s, userId)).orElse(false))
            .archived(Optional.ofNullable(entity.getArchived()).map(s -> booleanEncryptor.decryptEntity(s, userId)).orElse(false))
            .build();
    }

    @Override
    protected ListItemEntity processDomainConversion(ListItem domain) {
        String userId = uuidConverter.convertDomain(accessTokenProvider.get().getUserId());
        return ListItemEntity.builder()
            .listItemId(uuidConverter.convertDomain(domain.getListItemId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .parent(uuidConverter.convertDomain(domain.getParent()))
            .type(domain.getType())
            .title(stringEncryptor.encryptEntity(domain.getTitle(), userId))
            .pinned(booleanEncryptor.encryptEntity(domain.isPinned(), userId))
            .archived(booleanEncryptor.encryptEntity(domain.isArchived(), userId))
            .build();
    }

    public List<UnencryptedListItem> convertUnencrypted(Iterable<ListItemEntity> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
            .map(this::convertUnencrypted)
            .collect(Collectors.toList());

    }

    private UnencryptedListItem convertUnencrypted(ListItemEntity entity) {
        return UnencryptedListItem.builder()
            .listItemId(uuidConverter.convertEntity(entity.getListItemId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parent(uuidConverter.convertEntity(entity.getParent()))
            .type(entity.getType())
            .title(entity.getTitle())
            .pinned(entity.getPinned())
            .archived(entity.getArchived())
            .build();
    }
}
