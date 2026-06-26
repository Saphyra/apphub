package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.service;

import com.github.saphyra.apphub.api.feature.task_manager.model.organization.OrganizationResponse;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.Alm;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.PrincipalType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.Organization;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.OrganizationDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrganizationQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ORGANIZATION_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    @Mock
    private AlmDao almDao;

    @Mock
    private OrganizationDao organizationDao;

    @InjectMocks
    private OrganizationQueryService underTest;

    @Mock
    private Alm alm;

    @Mock
    private Organization organization;

    @Test
    void getOrganizationsOfUser() {
        given(almDao.getByUserIdAndObjectType(USER_ID, ObjectType.ORGANIZATION)).willReturn(List.of(alm));
        given(alm.getObjectId()).willReturn(ORGANIZATION_ID);
        given(organizationDao.getByIds(List.of(ORGANIZATION_ID))).willReturn(List.of(organization));
        given(organization.getId()).willReturn(ORGANIZATION_ID);
        given(organization.getName()).willReturn(NAME);
        given(organization.getDescription()).willReturn(DESCRIPTION);

        assertThat(underTest.getOrganizationsOfUser(USER_ID))
            .singleElement()
            .returns(ORGANIZATION_ID, OrganizationResponse::getOrganizationId)
            .returns(NAME, OrganizationResponse::getOrganizationName)
            .returns(DESCRIPTION, OrganizationResponse::getDescription);
    }

    @Test
    void getOrganization_noAlm(){
        given(almDao.findForObject(USER_ID, PrincipalType.USER, ORGANIZATION_ID, ObjectType.ORGANIZATION)).willReturn(Optional.empty());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.getOrganization(USER_ID, ORGANIZATION_ID));
    }

    @Test
    void getOrganization(){
        given(almDao.findForObject(USER_ID, PrincipalType.USER, ORGANIZATION_ID, ObjectType.ORGANIZATION)).willReturn(Optional.of(alm));
        given(alm.getObjectId()).willReturn(ORGANIZATION_ID);
        given(organizationDao.findByIdValidated(ORGANIZATION_ID)).willReturn(organization);
        given(organization.getId()).willReturn(ORGANIZATION_ID);
        given(organization.getName()).willReturn(NAME);
        given(organization.getDescription()).willReturn(DESCRIPTION);

        assertThat(underTest.getOrganization(USER_ID, ORGANIZATION_ID))
            .returns(ORGANIZATION_ID, OrganizationResponse::getOrganizationId)
            .returns(NAME, OrganizationResponse::getOrganizationName)
            .returns(DESCRIPTION, OrganizationResponse::getDescription);
    }
}