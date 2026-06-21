package com.github.saphyra.apphub.service.feature.task_manager.domain.alm.service;

import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.Alm;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.AlmFactory;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.Operation;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AlmService {
    private final AlmFactory almFactory;
    private final AlmDao almDao;

    public void grantOperations(UUID principal, PrincipalType principalType, UUID objectId, ObjectType objectType, List<Operation> operations) {
        Alm alm = almDao.findForObject(principal, principalType, objectId, objectType)
            .orElseGet(() -> almFactory.createAlm(principal, principalType, objectId, objectType, operations));

        operations.forEach(alm::addOperation);

        almDao.save(alm);
    }
}
