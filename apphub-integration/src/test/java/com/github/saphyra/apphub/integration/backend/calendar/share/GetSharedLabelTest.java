package com.github.saphyra.apphub.integration.backend.calendar.share;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarShareActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.ShareObjectRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectType;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedWithResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetSharedLabelTest extends BackEndTest {
    private static final String LABEL = "label";

    @Test(groups = {"be", "calendar"})
    public void getSharedLabel() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create label
        UUID labelId = CalendarLabelActions.createLabel(getServerPort(), ownerToken, LABEL)
            .getLabelId();

        //Get data of own unshared label
        SharedObjectResponse response = CalendarShareActions.getSharedObject(getServerPort(), ownerToken, SharedObjectType.LABEL, labelId, null);

        assertThat(response)
            .returns(labelId, SharedObjectResponse::getObjectId)
            .returns(LABEL, SharedObjectResponse::getName)
            .returns(List.of(), SharedObjectResponse::getSharedWith);
        UUID ownerId = response.getOwner();

        //Another user has no access to unshared label
        ResponseValidator.verifyNotFound(CalendarShareActions.getGetSharedObjectResponse(getServerPort(), sharedWithToken, SharedObjectType.LABEL, labelId, null));

        //Share label
        ShareObjectRequest request = ShareObjectRequest.builder()
            .type(SharedObjectType.LABEL)
            .objectId(labelId)
            .owner(ownerId)
            .sharedWith(sharedWithUserId)
            .grants(Set.of(Grant.VIEW))
            .parent(response.getParent())
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, request);

        //Get data of shared label
        response = CalendarShareActions.getSharedObject(getServerPort(), ownerToken, SharedObjectType.LABEL, labelId, null);
        assertThat(response)
            .returns(labelId, SharedObjectResponse::getObjectId)
            .returns(LABEL, SharedObjectResponse::getName)
            .returns(ownerId, SharedObjectResponse::getOwner);

        assertThat(response.getSharedWith())
            .singleElement()
            .returns(sharedWithUserId, SharedWithResponse::getUserId)
            .returns(Grant.VIEW, sharedWithResponse -> sharedWithResponse.getGrants().iterator().next())
            .returns(sharedWithData.getUsername(), SharedWithResponse::getUsername)
            .returns(sharedWithData.getEmail(), SharedWithResponse::getEmail);

        //SharedWith gets data of shared label
        response = CalendarShareActions.getSharedObject(getServerPort(), sharedWithToken, SharedObjectType.LABEL, labelId, null);
        assertThat(response)
            .returns(labelId, SharedObjectResponse::getObjectId)
            .returns(LABEL, SharedObjectResponse::getName)
            .returns(ownerId, SharedObjectResponse::getOwner);

        assertThat(response.getSharedWith())
            .singleElement()
            .returns(sharedWithUserId, SharedWithResponse::getUserId)
            .returns(Grant.VIEW, sharedWithResponse -> sharedWithResponse.getGrants().iterator().next())
            .returns(sharedWithData.getUsername(), SharedWithResponse::getUsername)
            .returns(sharedWithData.getEmail(), SharedWithResponse::getEmail);

        //Revoke view grant
        CalendarShareActions.editOperations(getServerPort(), ownerToken, SharedObjectType.LABEL, labelId, sharedWithUserId, Set.of(Grant.DELETE));

        ResponseValidator.verifyNotFound(CalendarShareActions.getGetSharedObjectResponse(getServerPort(), sharedWithToken, SharedObjectType.LABEL, labelId, null));
    }
}
