package com.github.saphyra.apphub.integration.backend.calendar.share.object;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
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

public class GetSharedObjectTest extends BackEndTest {
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

    @Test(groups = {"be", "calendar"})
    public void getSharedOccurrence() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create event
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME);
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, createEventRequest);
        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)
            .getFirst()
            .getOccurrenceId();

        //Get data of own unshared occurrence
        SharedObjectResponse response = CalendarShareActions.getSharedObject(getServerPort(), ownerToken, SharedObjectType.OCCURRENCE, occurrenceId, eventId);

        assertThat(response)
            .returns(occurrenceId, SharedObjectResponse::getObjectId)
            .returns(createEventRequest.getStartDate().toString(), SharedObjectResponse::getName)
            .returns(eventId, SharedObjectResponse::getParent)
            .returns(List.of(), SharedObjectResponse::getSharedWith);
        UUID ownerId = response.getOwner();

        //Another user has no access to unshared event
        ResponseValidator.verifyNotFound(CalendarShareActions.getGetSharedObjectResponse(getServerPort(), sharedWithToken, SharedObjectType.OCCURRENCE, occurrenceId, eventId));

        //Share occurrence
        ShareObjectRequest shareObjectRequest = ShareObjectRequest.builder()
            .type(SharedObjectType.OCCURRENCE)
            .objectId(occurrenceId)
            .owner(ownerId)
            .sharedWith(sharedWithUserId)
            .grants(Set.of(Grant.VIEW))
            .parent(eventId)
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareObjectRequest);

        //Get data of shared occurrence
        response = CalendarShareActions.getSharedObject(getServerPort(), ownerToken, SharedObjectType.OCCURRENCE, occurrenceId, eventId);
        assertThat(response)
            .returns(occurrenceId, SharedObjectResponse::getObjectId)
            .returns(createEventRequest.getStartDate().toString(), SharedObjectResponse::getName)
            .returns(eventId, SharedObjectResponse::getParent);

        assertThat(response.getSharedWith())
            .singleElement()
            .returns(sharedWithUserId, SharedWithResponse::getUserId)
            .returns(Grant.VIEW, sharedWithResponse -> sharedWithResponse.getGrants().iterator().next())
            .returns(sharedWithData.getUsername(), SharedWithResponse::getUsername)
            .returns(sharedWithData.getEmail(), SharedWithResponse::getEmail);

        //SharedWith gets data of shared event
        response = CalendarShareActions.getSharedObject(getServerPort(), sharedWithToken, SharedObjectType.OCCURRENCE, occurrenceId, eventId);
        assertThat(response)
            .returns(occurrenceId, SharedObjectResponse::getObjectId)
            .returns(createEventRequest.getStartDate().toString(), SharedObjectResponse::getName)
            .returns(eventId, SharedObjectResponse::getParent);

        assertThat(response.getSharedWith())
            .singleElement()
            .returns(sharedWithUserId, SharedWithResponse::getUserId)
            .returns(Grant.VIEW, sharedWithResponse -> sharedWithResponse.getGrants().iterator().next())
            .returns(sharedWithData.getUsername(), SharedWithResponse::getUsername)
            .returns(sharedWithData.getEmail(), SharedWithResponse::getEmail);

        //Revoke view grant
        CalendarShareActions.editOperations(getServerPort(), ownerToken, SharedObjectType.OCCURRENCE, occurrenceId, sharedWithUserId, Set.of(Grant.DELETE));

        ResponseValidator.verifyNotFound(CalendarShareActions.getGetSharedObjectResponse(getServerPort(), sharedWithToken, SharedObjectType.OCCURRENCE, occurrenceId, eventId));

        //See grant
        CalendarShareActions.editOperations(getServerPort(), ownerToken, SharedObjectType.OCCURRENCE, occurrenceId, sharedWithUserId, Set.of(Grant.SEE));

        response = CalendarShareActions.getSharedObject(getServerPort(), sharedWithToken, SharedObjectType.OCCURRENCE, occurrenceId, eventId);
        assertThat(response)
            .returns(occurrenceId, SharedObjectResponse::getObjectId)
            .returns(createEventRequest.getStartDate().toString(), SharedObjectResponse::getName)
            .returns(ownerId, SharedObjectResponse::getOwner);

        assertThat(response.getSharedWith())
            .singleElement()
            .returns(sharedWithUserId, SharedWithResponse::getUserId)
            .returns(Grant.SEE, sharedWithResponse -> sharedWithResponse.getGrants().iterator().next())
            .returns(sharedWithData.getUsername(), SharedWithResponse::getUsername)
            .returns(sharedWithData.getEmail(), SharedWithResponse::getEmail);
    }
}
