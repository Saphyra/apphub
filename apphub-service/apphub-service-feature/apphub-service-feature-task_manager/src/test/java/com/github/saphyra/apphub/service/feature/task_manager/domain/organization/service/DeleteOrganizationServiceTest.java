package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.service;

import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao.InvitationDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.OrganizationDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteOrganizationServiceTest {
    private static final UUID ORGANIZATION_ID = UUID.randomUUID();

    @Mock
    private AlmDao almDao;

    @Mock
    private InvitationDao invitationDao;

    @Mock
    private OrganizationDao organizationDao;

    @Mock
    private NotificationDao notificationDao;

    @InjectMocks
    private DeleteOrganizationService underTest;

    @Test
    void delete() {
        underTest.delete(ORGANIZATION_ID);

        then(almDao).should().deleteByObject(ORGANIZATION_ID, com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.ObjectType.ORGANIZATION);
        then(invitationDao).should().deleteByOrganizationId(ORGANIZATION_ID);
        then(notificationDao).should().deleteByOrganizationId(ORGANIZATION_ID);
        then(organizationDao).should().delete(ORGANIZATION_ID);
    }
}