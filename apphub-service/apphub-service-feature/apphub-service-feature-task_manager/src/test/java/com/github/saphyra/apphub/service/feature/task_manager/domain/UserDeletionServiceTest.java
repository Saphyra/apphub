package com.github.saphyra.apphub.service.feature.task_manager.domain;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.Alm;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.Operation;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.service.DeleteOrganizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;

@ExtendWith(MockitoExtension.class)
class UserDeletionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TO_KEEP_ORGANIZATION_ID = UUID.randomUUID();
    private static final UUID TO_DELETE_ORGANIZATION_ID = UUID.randomUUID();

    @Mock
    private AlmDao almDao;

    @Mock
    private DeleteOrganizationService deleteOrganizationService;

    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(mock(ErrorReporterService.class));

    @Mock
    private DeleteByUserIdDao deleteByUserIdDao;

    private UserDeletionService underTest;

    @Mock
    private Alm usersToKeepOrgnaizationAlm;

    @Mock
    private Alm otherToKeepOrgnaizationAlm;

    @Mock
    private Alm toDeleteOrganizationAlm;

    @BeforeEach
    void setUp() {
        underTest = new UserDeletionService(almDao, deleteOrganizationService, executorServiceBean, List.of(deleteByUserIdDao));
    }

    @Test
    void delete() {
        given(almDao.getByUserIdAndObjectType(USER_ID, ObjectType.ORGANIZATION)).willReturn(List.of(usersToKeepOrgnaizationAlm, toDeleteOrganizationAlm));
        given(usersToKeepOrgnaizationAlm.getObjectId()).willReturn(TO_KEEP_ORGANIZATION_ID);
        given(toDeleteOrganizationAlm.getObjectId()).willReturn(TO_DELETE_ORGANIZATION_ID);

        given(almDao.getByObjects(List.of(TO_KEEP_ORGANIZATION_ID, TO_DELETE_ORGANIZATION_ID), ObjectType.ORGANIZATION))
            .willReturn(List.of(otherToKeepOrgnaizationAlm, usersToKeepOrgnaizationAlm, toDeleteOrganizationAlm));
        given(usersToKeepOrgnaizationAlm.getPrincipal()).willReturn(USER_ID);
        given(otherToKeepOrgnaizationAlm.getPrincipal()).willReturn(UUID.randomUUID());
        given(toDeleteOrganizationAlm.getPrincipal()).willReturn(USER_ID);
        given(usersToKeepOrgnaizationAlm.getOperations()).willReturn(List.of(Operation.OWNER));
        given(otherToKeepOrgnaizationAlm.getOperations()).willReturn(List.of(Operation.OWNER));
        given(toDeleteOrganizationAlm.getOperations()).willReturn(List.of(Operation.OWNER));

        underTest.delete(USER_ID);

        then(deleteByUserIdDao).should().deleteByUserId(USER_ID);
        then(deleteOrganizationService).should(timeout(1000)).delete(TO_DELETE_ORGANIZATION_ID);
    }
}