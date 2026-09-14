package com.github.saphyra.apphub.integration.backend.task_manager;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.task_manager.TaskManagerOrganizationActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.task_manager.organization.CreateOrganizationRequest;
import com.github.saphyra.apphub.integration.structure.api.task_manager.organization.OrganizationResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskManagerOrganizationTest extends BackEndTest {
    private static final String ORGANIZATION_NAME = "organization-name";
    private static final String DESCRIPTION = "description";

    @Test(groups = {"be", "task-manager"})
    public void organizationCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_blankOrganizationName(accessToken);
        create_tooLongOrganizationName(accessToken);
        create_nullDescription(accessToken);
        create_tooLongDescription(accessToken);
        create_nullInvitedUsers(accessToken);
        create_invitedUsersContainsNull(accessToken);
        create_invitedUserDoesNotExist(accessToken);
        create_selfInvitation(accessToken, userData);
        create(accessToken);
        UUID organizationId = getOrganizations(accessToken);
        getOrganization(accessToken, organizationId);
    }

    private void getOrganization(String accessToken, UUID organizationId) {
        OrganizationResponse response = TaskManagerOrganizationActions.getOrganization(getServerPort(), accessToken, organizationId);

        assertThat(response)
            .returns(ORGANIZATION_NAME, OrganizationResponse::getOrganizationName)
            .returns(DESCRIPTION, OrganizationResponse::getDescription);
    }

    private UUID getOrganizations(String accessToken) {
        List<OrganizationResponse> organizations = TaskManagerOrganizationActions.getOrganizations(getServerPort(), accessToken);

        assertThat(organizations)
            .singleElement()
            .returns(ORGANIZATION_NAME, OrganizationResponse::getOrganizationName)
            .returns(DESCRIPTION, OrganizationResponse::getDescription);

        return organizations.getFirst()
            .getOrganizationId();
    }

    private void create(String accessToken) {
        CreateOrganizationRequest request = validRequest();

        TaskManagerOrganizationActions.createOrganization(getServerPort(), accessToken, request);
    }

    private void create_selfInvitation(String accessToken, RegistrationParameters userData) {
        UUID userId = UserDynamoDbRepository.getUserIdByEmail(userData.getEmail());

        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .invitedUsers(List.of(userId))
            .build();

        ResponseValidator.verifyInvalidParam(TaskManagerOrganizationActions.createOrganizationResponse(getServerPort(), accessToken, request), "invitedUser", "cannot invite yourself");
    }

    private void create_invitedUserDoesNotExist(String accessToken) {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .invitedUsers(List.of(UUID.randomUUID()))
            .build();

        ResponseValidator.verifyInvalidParam(TaskManagerOrganizationActions.createOrganizationResponse(getServerPort(), accessToken, request), "invitedUser", "does not exist");
    }

    private void create_invitedUsersContainsNull(String accessToken) {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .invitedUsers(CollectionUtils.toList(UUID.randomUUID(), null))
            .build();

        ResponseValidator.verifyInvalidParam(TaskManagerOrganizationActions.createOrganizationResponse(getServerPort(), accessToken, request), "invitedUsers", "must not contain null values");
    }

    private void create_nullInvitedUsers(String accessToken) {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .invitedUsers(null)
            .build();

        ResponseValidator.verifyInvalidParam(TaskManagerOrganizationActions.createOrganizationResponse(getServerPort(), accessToken, request), "invitedUsers", "must not be null");
    }

    private void create_tooLongDescription(String accessToken) {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .description("a".repeat(Constants.MAX_ORGANIZATION_DESCRIPTION_LENGTH + 1))
            .build();

        ResponseValidator.verifyInvalidParam(TaskManagerOrganizationActions.createOrganizationResponse(getServerPort(), accessToken, request), "description", "too long");
    }

    private void create_nullDescription(String accessToken) {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .description(null)
            .build();

        ResponseValidator.verifyInvalidParam(TaskManagerOrganizationActions.createOrganizationResponse(getServerPort(), accessToken, request), "description", "must not be null");
    }

    private void create_tooLongOrganizationName(String accessToken) {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .organizationName("a".repeat(Constants.MAX_ORGANIZATION_NAME_LENGTH + 1))
            .build();

        ResponseValidator.verifyInvalidParam(TaskManagerOrganizationActions.createOrganizationResponse(getServerPort(), accessToken, request), "organizationName", "too long");
    }

    private void create_blankOrganizationName(String accessToken) {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .organizationName(" ")
            .build();

        ResponseValidator.verifyInvalidParam(TaskManagerOrganizationActions.createOrganizationResponse(getServerPort(), accessToken, request), "organizationName", "must not be null or blank");
    }

    private CreateOrganizationRequest validRequest() {
        return CreateOrganizationRequest.builder()
            .organizationName(ORGANIZATION_NAME)
            .description(DESCRIPTION)
            .invitedUsers(List.of())
            .build();
    }
}
