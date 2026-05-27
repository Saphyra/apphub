package com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
@Deprecated(forRemoval = true)
class DeprecatedListItemConverter extends ConverterBase<ListItemEntity, DeprecatedListItem> {
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_PINNED = "pinned";
    static final String COLUMN_ARCHIVED = "archived";

    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final BooleanEncryptor booleanEncryptor;

    @Override
    protected DeprecatedListItem processEntityConversion(ListItemEntity entity) {
        String userId = uuidConverter.convertDomain(accessTokenProvider.get().getUserId());

        return DeprecatedListItem.builder()
            .listItemId(uuidConverter.convertEntity(entity.getListItemId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parent(uuidConverter.convertEntity(entity.getParent()))
            .type(entity.getType())
            .title(stringEncryptor.decrypt(entity.getTitle(), userId, entity.getListItemId(), COLUMN_TITLE))
            .pinned(Optional.ofNullable(booleanEncryptor.decrypt(entity.getPinned(), userId, entity.getListItemId(), COLUMN_PINNED)).orElse(false))
            .archived(Optional.ofNullable(booleanEncryptor.decrypt(entity.getArchived(), userId, entity.getListItemId(), COLUMN_ARCHIVED)).orElse(false))
            .build();
    }

    @Override
    protected ListItemEntity processDomainConversion(DeprecatedListItem domain) {
        String userId = uuidConverter.convertDomain(accessTokenProvider.get().getUserId());
        String listItemId = uuidConverter.convertDomain(domain.getListItemId());
        return ListItemEntity.builder()
            .listItemId(listItemId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .parent(uuidConverter.convertDomain(domain.getParent()))
            .type(domain.getType())
            .title(stringEncryptor.encrypt(domain.getTitle(), userId, listItemId, COLUMN_TITLE))
            .pinned(booleanEncryptor.encrypt(domain.isPinned(), userId, listItemId, COLUMN_PINNED))
            .archived(booleanEncryptor.encrypt(domain.isArchived(), userId, listItemId, COLUMN_ARCHIVED))
            .build();
    }
}
