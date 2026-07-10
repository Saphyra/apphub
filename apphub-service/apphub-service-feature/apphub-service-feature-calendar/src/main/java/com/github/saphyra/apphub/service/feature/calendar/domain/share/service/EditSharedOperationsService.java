package com.github.saphyra.apphub.service.feature.calendar.domain.share.service;

import com.github.saphyra.apphub.api.feature.calendar.model.Operation;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class EditSharedOperationsService {
    private final AlmDao almDao;

    public void editSharedOperations(UUID userId, UUID sharedWith, SharedObjectType type, UUID objectId, List<Operation> operations) {
        //TODO verify user has permission to modify roles

        Alm alm = almDao.findForObjectValidated(sharedWith, PrincipalType.USER, objectId,type);

        alm.setOperations(operations);
        almDao.save(alm);
    }
}
