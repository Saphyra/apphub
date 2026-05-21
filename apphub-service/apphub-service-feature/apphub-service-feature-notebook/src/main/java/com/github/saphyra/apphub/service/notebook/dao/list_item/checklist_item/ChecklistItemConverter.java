package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_CHECKED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_INDEX;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ChecklistItemConverter extends ConverterBase<ChecklistItemEntity, ChecklistItem> {
    private final BooleanEncryptor booleanEncryptor;
    private final IntegerEncryptor integerEncryptor;
    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;

    @Override
    protected ChecklistItemEntity processDomainConversion(ChecklistItem domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String checklistItemId = uuidConverter.convertDomain(domain.getChecklistItemId());

        return ChecklistItemEntity.builder()
            .listItemId(uuidConverter.convertDomain(domain.getListItemId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .checklistItemId(checklistItemId)
            .checked(booleanEncryptor.encrypt(domain.isChecked(), userId, checklistItemId, COLUMN_CHECKED))
            .index(integerEncryptor.encrypt(domain.getIndex(), userId, checklistItemId, COLUMN_INDEX))
            .build();
    }

    @Override
    protected ChecklistItem processEntityConversion(ChecklistItemEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();

        return ChecklistItem.builder()
            .listItemId(uuidConverter.convertEntity(entity.getListItemId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .checklistItemId(uuidConverter.convertEntity(entity.getChecklistItemId()))
            .checked(Boolean.TRUE.equals(booleanEncryptor.decrypt(entity.getChecked(), userId, entity.getChecklistItemId(), COLUMN_CHECKED)))
            .index(integerEncryptor.decrypt(entity.getIndex(), userId, entity.getChecklistItemId(), COLUMN_INDEX))
            .build();
    }
}


