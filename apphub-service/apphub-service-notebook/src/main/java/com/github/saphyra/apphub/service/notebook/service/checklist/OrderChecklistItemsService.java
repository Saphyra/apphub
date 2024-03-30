package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.query.ChecklistQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderChecklistItemsService {
    private final ChecklistQueryService checklistQueryService;
    private final DimensionDao dimensionDao;
    private final ContentDao contentDao;

    public ChecklistResponse orderItems(UUID listItemId) {
        List<Dimension> ordered = dimensionDao.getByExternalReference(listItemId)
            .stream()
            .map(dimension -> new BiWrapper<>(dimension, contentDao.findByParentValidated(dimension.getDimensionId()).getContent()))
            .sorted(Comparator.comparing(o -> o.getEntity2().toLowerCase()))
            .map(BiWrapper::getEntity1)
            .toList();

        for (int i = 0; i < ordered.size(); i++) {
            Dimension dimension = ordered.get(i);
            dimension.setIndex(i);
        }

        dimensionDao.saveAll(ordered);

        return checklistQueryService.getChecklistResponse(listItemId);
    }
}
