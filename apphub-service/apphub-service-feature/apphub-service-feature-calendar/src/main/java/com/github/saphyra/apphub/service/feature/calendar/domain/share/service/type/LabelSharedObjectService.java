package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
class LabelSharedObjectService implements SharedObjectService {
    private final LabelDao labelDao;

    @Override
    public SharedObjectType getType() {
        return SharedObjectType.LABEL;
    }

    @Override
    @SneakyThrows
    public SharedObject getSharedObject(UUID owner, UUID objectId) {
        Label label = labelDao.findByIdValidated(owner, objectId);

        return new SharedObject(label.getLabelId(), label.getUserId(), label.getUserId(), label.getLabel());
    }
}
