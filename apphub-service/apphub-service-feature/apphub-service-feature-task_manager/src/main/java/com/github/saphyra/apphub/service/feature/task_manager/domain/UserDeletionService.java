package com.github.saphyra.apphub.service.feature.task_manager.domain;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.Alm;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.Operation;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.service.DeleteOrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserDeletionService {
    private final AlmDao almDao;
    private final DeleteOrganizationService deleteOrganizationService;
    private final ExecutorServiceBean executorServiceBean;
    private final List<DeleteByUserIdDao> deleteByUserIdDaos;

    public void delete(UUID userId) {
        List<UUID> organizationsToDelete = getOrganizationsToDelete(userId);

        deleteByUserIdDaos.forEach(dao -> dao.deleteByUserId(userId));

        organizationsToDelete.forEach(organizationId -> executorServiceBean.execute(() -> deleteOrganizationService.delete(organizationId)));
    }

    private List<UUID> getOrganizationsToDelete(UUID userId) {
        List<UUID> ownedOrganizations = almDao.getByUserIdAndObjectType(userId, ObjectType.ORGANIZATION)
            .stream()
            .filter(alm -> alm.getOperations().contains(Operation.OWNER))
            .map(Alm::getObjectId)
            .toList();

        List<UUID> organizationsWithOwnerLeft = almDao.getByObjects(ownedOrganizations, ObjectType.ORGANIZATION)
            .stream()
            .filter(alm -> !alm.getPrincipal().equals(userId))
            .filter(alm -> alm.getOperations().contains(Operation.OWNER))
            .map(Alm::getObjectId)
            .toList();

        return ownedOrganizations.stream()
            .filter(organizationId -> !organizationsWithOwnerLeft.contains(organizationId))
            .toList();
    }
}
