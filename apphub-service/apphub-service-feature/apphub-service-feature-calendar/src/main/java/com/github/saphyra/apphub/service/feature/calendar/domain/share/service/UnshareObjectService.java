package com.github.saphyra.apphub.service.feature.calendar.domain.share.service;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class UnshareObjectService {
    private final AlmDao almDao;

    public void unshareObject(UUID userId, UUID sharedWith, SharedObjectType type, UUID objectId) {
        //TODO verify permission

        almDao.findForObject(sharedWith, PrincipalType.USER, objectId, type)
            .ifPresent(almDao::delete);
    }
}
