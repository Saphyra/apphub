package com.github.saphyra.apphub.service.feature.task_manager.domain.organization;

import com.github.saphyra.apphub.api.feature.task_manager.model.organization.CreateOrganizationRequest;
import com.github.saphyra.apphub.api.feature.task_manager.model.organization.OrganizationResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.service.CreateOrganizationService;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.service.OrganizationQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OrganizationControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ORGANIZATION_ID = UUID.randomUUID();

    @Mock
    private CreateOrganizationService createOrganizationService;

    @Mock
    private OrganizationQueryService organizationQueryService;

    @InjectMocks
    private OrganizationControllerImpl underTest;

    @Mock
    private AccessToken accessToken;

    @Mock
    private CreateOrganizationRequest createOrganizationRequest;

    @Mock
    private OrganizationResponse organizationResponse;

    @Test
    void createOrganization() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.createOrganization(createOrganizationRequest, accessToken);

        then(createOrganizationService).should().createOrganization(USER_ID, createOrganizationRequest);
    }

    @Test
    void getOrganizations() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(organizationQueryService.getOrganizationsOfUser(USER_ID)).willReturn(List.of(organizationResponse));

        assertThat(underTest.getOrganizations(accessToken)).containsExactly(organizationResponse);
    }

    @Test
    void getOrganization() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(organizationQueryService.getOrganization(USER_ID, ORGANIZATION_ID)).willReturn(organizationResponse);

        assertThat(underTest.getOrganization(ORGANIZATION_ID, accessToken)).isEqualTo(organizationResponse);
    }
}