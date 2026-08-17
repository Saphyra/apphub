package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.service.LabelObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class LabelSharedObjectService implements SharedObjectService {
    private final LabelObjectQueryService labelObjectQueryService;
    private final LabelDao labelDao;

    @Override
    public SharedObjectType getType() {
        return SharedObjectType.LABEL;
    }

    @Override
    public SharedObject getSharedObject(UUID userId, UUID labelId, UUID parent) {
        Label label = labelObjectQueryService.findLabel(userId, labelId, Grant.VIEW)
            .orElseThrow(() -> ExceptionFactory.notFound("Label not found for owner %s and labelId %s".formatted(userId, labelId)));

        return new SharedObject(label.getLabelId(), label.getUserId(), label.getUserId(), label.getLabel());
    }

    @Override
    public boolean exists(UUID userId, UUID labelId) {
        return labelDao.findById(userId, labelId)
            .isPresent();
    }
}
