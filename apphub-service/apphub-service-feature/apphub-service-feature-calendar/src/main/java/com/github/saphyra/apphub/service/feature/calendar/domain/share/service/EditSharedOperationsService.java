package com.github.saphyra.apphub.service.feature.calendar.domain.share.service;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EditSharedOperationsService {
    private final AlmDao almDao;

    public void editSharedOperations(UUID userId, UUID sharedWith, SharedObjectType type, UUID objectId, Set<Grant> grants) {
        Alm alm = almDao.findForObjectValidated(sharedWith, PrincipalType.USER, objectId, type);

        if (!alm.getOwner().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not edit Alm");
        }

        alm.setGrants(grants);
        almDao.save(alm);
    }
}
