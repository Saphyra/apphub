package com.github.saphyra.apphub.service.notebook.migration.checklist;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@ForRemoval("notebook-redesign")
//TODO unit test
public class ChecklistMigrationService {
    private final ChecklistItemDao checklistItemDao;
    private final AccessTokenProvider accessTokenProvider;
    private final DimensionFactory dimensionFactory;
    private final DimensionDao dimensionDao;
    private final CheckedItemFactory checkedItemFactory;
    private final CheckedItemDao checkedItemDao;

    @Transactional
    public void migrate() {
        checklistItemDao.getAllUnencrypted()
            .stream()
            .map(UnencryptedChecklistItem::getUserId)
            .forEach(this::migrate);

    }

    private void migrate(UUID userId) {
        try {
            accessTokenProvider.set(AccessTokenHeader.builder().userId(userId).build());

            checklistItemDao.getByUserId(userId)
                .forEach(this::migrate);
        } finally {
            accessTokenProvider.clear();
        }
    }

    private void migrate(ChecklistItem checklistItem) {
        Dimension dimension = dimensionFactory.create(checklistItem.getUserId(), checklistItem.getParent(), checklistItem.getOrder(), checklistItem.getChecklistItemId());
        dimensionDao.save(dimension);

        CheckedItem checkedItem = checkedItemFactory.create(checklistItem.getUserId(), dimension.getDimensionId(), checklistItem.getChecked());
        checkedItemDao.save(checkedItem);
    }
}
