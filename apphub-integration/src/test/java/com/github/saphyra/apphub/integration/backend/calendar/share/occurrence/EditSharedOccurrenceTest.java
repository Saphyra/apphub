package com.github.saphyra.apphub.integration.backend.calendar.share.occurrence;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarShareActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.action.backend.calendar.OccurrenceRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.calendar.ShareObjectRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EditSharedOccurrenceTest extends BackEndTest {
    private static final String LABEL = "label";
    private static final String NOTE = "note";

    @Test(groups = {"be", "calendar"}, dataProvider = "editSharedOccurrenceData")
    public void editSharedOccurrence(Set<Grant> labelGrants, Set<Grant> eventGrants, Set<Grant> occurrenceGrants) {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create event
        UUID labelId = CalendarLabelActions.createLabel(getServerPort(), ownerToken, LABEL)
            .getLabelId();
        EventRequest eventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(Map.of(labelId, ownerId))
            .build();
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, eventRequest);
        OccurrenceResponse occurrenceResponse = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)
            .getFirst();
        UUID occurrenceId = occurrenceResponse
            .getOccurrenceId();

        //Share label
        if (!labelGrants.isEmpty()) {
            ShareObjectRequest shareLabelRequest = ShareObjectRequest.builder()
                .sharedWith(sharedWithUserId)
                .owner(ownerId)
                .objectId(labelId)
                .parent(ownerId)
                .type(SharedObjectType.LABEL)
                .grants(labelGrants)
                .build();
            CalendarShareActions.shareObject(getServerPort(), ownerToken, shareLabelRequest);
        }

        //Share event
        if (!eventGrants.isEmpty()) {
            ShareObjectRequest shareEventRequest = ShareObjectRequest.builder()
                .sharedWith(sharedWithUserId)
                .owner(ownerId)
                .objectId(eventId)
                .parent(ownerId)
                .type(SharedObjectType.EVENT)
                .grants(eventGrants)
                .build();
            CalendarShareActions.shareObject(getServerPort(), ownerToken, shareEventRequest);
        }

        if (!occurrenceGrants.isEmpty()) {
            //Share occurrence
            ShareObjectRequest shareOccurrenceRequest = ShareObjectRequest.builder()
                .sharedWith(sharedWithUserId)
                .owner(ownerId)
                .objectId(occurrenceId)
                .parent(eventId)
                .type(SharedObjectType.OCCURRENCE)
                .grants(occurrenceGrants)
                .build();
            CalendarShareActions.shareObject(getServerPort(), ownerToken, shareOccurrenceRequest);
        }

        OccurrenceRequest occurrenceRequest = OccurrenceRequestFactory.fromResponse(occurrenceResponse)
            .note(NOTE)
            .build();
        CalendarOccurrenceActions.editOccurrence(getServerPort(), sharedWithToken, eventId, occurrenceId, occurrenceRequest);

        assertThat(CalendarOccurrenceActions.getOccurrence(getServerPort(), ownerToken, eventId, occurrenceId))
            .returns(NOTE, OccurrenceResponse::getNote);
    }

    @Test(groups = {"be", "calendar"}, dataProvider = "editSharedOccurrence_noGrantData")
    public void editSharedOccurrence_noGrant(Set<Grant> labelGrants, Set<Grant> eventGrants, Set<Grant> occurrenceGrants) {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create event
        UUID labelId = CalendarLabelActions.createLabel(getServerPort(), ownerToken, LABEL)
            .getLabelId();
        EventRequest eventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(Map.of(labelId, ownerId))
            .build();
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, eventRequest);
        OccurrenceResponse occurrenceResponse = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)
            .getFirst();
        UUID occurrenceId = occurrenceResponse
            .getOccurrenceId();

        //Share label
        if (!labelGrants.isEmpty()) {
            ShareObjectRequest shareLabelRequest = ShareObjectRequest.builder()
                .sharedWith(sharedWithUserId)
                .owner(ownerId)
                .objectId(labelId)
                .parent(ownerId)
                .type(SharedObjectType.LABEL)
                .grants(labelGrants)
                .build();
            CalendarShareActions.shareObject(getServerPort(), ownerToken, shareLabelRequest);
        }

        //Share event
        if (!eventGrants.isEmpty()) {
            ShareObjectRequest shareEventRequest = ShareObjectRequest.builder()
                .sharedWith(sharedWithUserId)
                .owner(ownerId)
                .objectId(eventId)
                .parent(ownerId)
                .type(SharedObjectType.EVENT)
                .grants(eventGrants)
                .build();
            CalendarShareActions.shareObject(getServerPort(), ownerToken, shareEventRequest);
        }

        if (!occurrenceGrants.isEmpty()) {
            //Share occurrence
            ShareObjectRequest shareOccurrenceRequest = ShareObjectRequest.builder()
                .sharedWith(sharedWithUserId)
                .owner(ownerId)
                .objectId(occurrenceId)
                .parent(eventId)
                .type(SharedObjectType.OCCURRENCE)
                .grants(occurrenceGrants)
                .build();
            CalendarShareActions.shareObject(getServerPort(), ownerToken, shareOccurrenceRequest);
        }

        OccurrenceRequest occurrenceRequest = OccurrenceRequestFactory.fromResponse(occurrenceResponse)
            .note(NOTE)
            .build();
        ResponseValidator.verifyNotFound(CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), sharedWithToken, eventId, occurrenceId, occurrenceRequest));
    }

    @DataProvider(parallel = true)
    public static Object[][] editSharedOccurrenceData() {
        return new Object[][]{
            new Object[]{Set.of(), Set.of(), Set.of(Grant.VIEW, Grant.EDIT)},
            new Object[]{Set.of(), Set.of(Grant.EDIT_CHILDREN, Grant.VIEW_CHILDREN), Set.of()},
            new Object[]{Set.of(Grant.EDIT_CHILDREN, Grant.VIEW_CHILDREN), Set.of(), Set.of()},
            new Object[]{Set.of(), Set.of(Grant.EDIT_CHILDREN), Set.of(Grant.VIEW)},
            new Object[]{Set.of(), Set.of(Grant.VIEW_CHILDREN), Set.of(Grant.EDIT)},
            new Object[]{Set.of(Grant.EDIT_CHILDREN), Set.of(), Set.of(Grant.VIEW)},
            new Object[]{Set.of(Grant.VIEW_CHILDREN), Set.of(), Set.of(Grant.EDIT)},
            new Object[]{Set.of(Grant.VIEW_CHILDREN), Set.of(Grant.EDIT_CHILDREN), Set.of()},
            new Object[]{Set.of(Grant.EDIT_CHILDREN), Set.of(Grant.VIEW_CHILDREN), Set.of()},
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] editSharedOccurrence_noGrantData() {
        return new Object[][]{
            new Object[]{Set.of(), Set.of(), Set.of(Grant.VIEW)},
            new Object[]{Set.of(), Set.of(Grant.VIEW_CHILDREN), Set.of()},
            new Object[]{Set.of(Grant.VIEW_CHILDREN), Set.of(), Set.of()},
            new Object[]{Set.of(), Set.of(), Set.of(Grant.EDIT)},
            new Object[]{Set.of(), Set.of(Grant.EDIT_CHILDREN), Set.of()},
            new Object[]{Set.of(Grant.EDIT_CHILDREN), Set.of(), Set.of()},
        };
    }
}
