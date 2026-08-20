package com.github.saphyra.apphub.integration.backend.calendar.share;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarShareActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
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

public class GetSharedEventTest extends BackEndTest {
    @Test(groups = {"be", "calendar"})
    public void getSharedEvent(){
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create event
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME);
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, createEventRequest);

        //Get data of own unshared event
        SharedObjectResponse response = CalendarShareActions.getSharedObject(getServerPort(), ownerToken, SharedObjectType.EVENT, eventId, null);

        assertThat(response)
            .returns(eventId, SharedObjectResponse::getObjectId)
            .returns(createEventRequest.getTitle(), SharedObjectResponse::getName)
            .returns(List.of(), SharedObjectResponse::getSharedWith);
        UUID ownerId = response.getOwner();

        //Another user has no access to unshared event
        ResponseValidator.verifyNotFound(CalendarShareActions.getGetSharedObjectResponse(getServerPort(), sharedWithToken, SharedObjectType.EVENT, eventId, null));

        //Share event
        ShareObjectRequest shareObjectRequest = ShareObjectRequest.builder()
            .type(SharedObjectType.EVENT)
            .objectId(eventId)
            .owner(ownerId)
            .sharedWith(sharedWithUserId)
            .grants(Set.of(Grant.VIEW))
            .parent(response.getParent())
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareObjectRequest);

        //Get data of shared event
        response = CalendarShareActions.getSharedObject(getServerPort(), ownerToken, SharedObjectType.EVENT, eventId, null);
        assertThat(response)
            .returns(eventId, SharedObjectResponse::getObjectId)
            .returns(createEventRequest.getTitle(), SharedObjectResponse::getName)
            .returns(ownerId, SharedObjectResponse::getOwner);

        assertThat(response.getSharedWith())
            .singleElement()
            .returns(sharedWithUserId, SharedWithResponse::getUserId)
            .returns(Grant.VIEW, sharedWithResponse -> sharedWithResponse.getGrants().iterator().next())
            .returns(sharedWithData.getUsername(), SharedWithResponse::getUsername)
            .returns(sharedWithData.getEmail(), SharedWithResponse::getEmail);

        //SharedWith gets data of shared event
        response = CalendarShareActions.getSharedObject(getServerPort(), sharedWithToken, SharedObjectType.EVENT, eventId, null);
        assertThat(response)
            .returns(eventId, SharedObjectResponse::getObjectId)
            .returns(createEventRequest.getTitle(), SharedObjectResponse::getName)
            .returns(ownerId, SharedObjectResponse::getOwner);

        assertThat(response.getSharedWith())
            .singleElement()
            .returns(sharedWithUserId, SharedWithResponse::getUserId)
            .returns(List.of(Grant.VIEW), SharedWithResponse::getGrants)
            .returns(sharedWithData.getUsername(), SharedWithResponse::getUsername)
            .returns(sharedWithData.getEmail(), SharedWithResponse::getEmail);

        //Revoke view grant
        CalendarShareActions.editOperations(getServerPort(), ownerToken, SharedObjectType.EVENT, eventId, sharedWithUserId, Set.of(Grant.DELETE));

        ResponseValidator.verifyNotFound(CalendarShareActions.getGetSharedObjectResponse(getServerPort(), sharedWithToken, SharedObjectType.EVENT, eventId, null));

        //See grant
        CalendarShareActions.editOperations(getServerPort(), ownerToken, SharedObjectType.EVENT, eventId, sharedWithUserId, Set.of(Grant.SEE));

        response = CalendarShareActions.getSharedObject(getServerPort(), sharedWithToken, SharedObjectType.EVENT, eventId, null);
        assertThat(response)
            .returns(eventId, SharedObjectResponse::getObjectId)
            .returns(Constants.QUESTION_MARK, SharedObjectResponse::getName)
            .returns(ownerId, SharedObjectResponse::getOwner);

        assertThat(response.getSharedWith())
            .singleElement()
            .returns(sharedWithUserId, SharedWithResponse::getUserId)
            .returns(List.of(Grant.SEE), SharedWithResponse::getGrants)
            .returns(sharedWithData.getUsername(), SharedWithResponse::getUsername)
            .returns(sharedWithData.getEmail(), SharedWithResponse::getEmail);
    }
}
