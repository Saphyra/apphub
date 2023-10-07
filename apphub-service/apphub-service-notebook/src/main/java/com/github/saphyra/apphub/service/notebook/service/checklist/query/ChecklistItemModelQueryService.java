package com.github.saphyra.apphub.service.notebook.service.checklist.query;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ChecklistItemModelQueryService {
    private final DimensionDao dimensionDao;
    private final CheckedItemDao checkedItemDao;
    private final ContentDao contentDao;

    public List<ChecklistItemModel> getItems(UUID listItemId) {
        return dimensionDao.getByExternalReference(listItemId)
            .stream()
            .map(this::fetchData)
            .collect(Collectors.toList());
    }

    private ChecklistItemModel fetchData(Dimension dimension) {
        return ChecklistItemModel.builder()
            .checklistItemId(dimension.getDimensionId())
            .index(dimension.getIndex())
            .checked(checkedItemDao.findByIdValidated(dimension.getDimensionId()).getChecked())
            .content(contentDao.findByParentValidated(dimension.getDimensionId()).getContent())
            .type(ItemType.EXISTING)
            .build();
    }
}
