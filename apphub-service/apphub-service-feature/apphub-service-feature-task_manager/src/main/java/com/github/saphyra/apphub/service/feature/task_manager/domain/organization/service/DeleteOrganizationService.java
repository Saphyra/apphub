package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.service;

import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao.InvitationDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.OrganizationDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteOrganizationService {
    private final AlmDao almDao;
    private final InvitationDao invitationDao;
    private final OrganizationDao organizationDao;
    private final NotificationDao notificationDao;

    public void delete(UUID organizationId) {
        almDao.deleteByObject(organizationId, ObjectType.ORGANIZATION);
        invitationDao.deleteByOrganizationId(organizationId);
        notificationDao.deleteByOrganizationId(organizationId);
        organizationDao.delete(organizationId);
    }
}
