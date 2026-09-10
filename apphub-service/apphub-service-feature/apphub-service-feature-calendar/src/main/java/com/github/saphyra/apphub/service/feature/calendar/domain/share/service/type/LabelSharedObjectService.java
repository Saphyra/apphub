package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.service.LabelObjectQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
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
    public Optional<SharedObject> getSharedObject(UUID principal, UUID labelId, UUID parent) {
        return labelObjectQueryService.findLabel(principal, labelId, Grant.VIEW)
            .map(label -> new SharedObject(label.getLabelId(), label.getUserId(), label.getUserId(), label.getLabel()));
    }

    @Override
    public boolean exists(UUID userId, UUID labelId) {
        return labelDao.findById(userId, labelId)
            .isPresent();
    }
}
