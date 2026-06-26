package com.github.saphyra.apphub.service.feature.task_manager;

import com.github.saphyra.apphub.lib.config.common.GenericEndpoints;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao.InvitationDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao.NotificationDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.OrganizationDao;
import com.github.saphyra.apphub.test.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.rest_assured.UrlFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskManagerApplicationTest {
    @LocalServerPort
    private int serverPort;

    @MockitoBean
    private AlmDao almDao;

    @MockitoBean
    private InvitationDao invitationDao;

    @MockitoBean
    private NotificationDao notificationDao;

    @MockitoBean
    private OrganizationDao organizationDao;

    @Test
    public void startup() {
        int statusCode = RequestFactory.createRequest()
            .get(UrlFactory.create(serverPort, GenericEndpoints.HEALTH))
            .getStatusCode();

        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
    }
}