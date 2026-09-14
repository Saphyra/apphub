package com.github.saphyra.apphub.integration.backend.calendar.share.occurrence;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarShareActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
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

public class DeleteSharedOccurrenceTest extends BackEndTest {
    private static final String LABEL = "label";

    @Test(groups = {"be", "calendar"}, dataProvider = "deleteSharedOccurrenceData")
    public void deleteSharedOccurrence(Set<Grant> labelGrants, Set<Grant> eventGrants, Set<Grant> occurrenceGrants) {
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

        CalendarOccurrenceActions.deleteOccurrence(getServerPort(), sharedWithToken, eventId, occurrenceId);

        assertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)).isEmpty();
    }

    @Test(groups = {"be", "calendar"})
    public void deleteSharedOccurrence_noGrant() {
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

        //Share occurrence
        ShareObjectRequest shareOccurrenceRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(occurrenceId)
            .parent(eventId)
            .type(SharedObjectType.OCCURRENCE)
            .grants(Set.of(Grant.VIEW))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareOccurrenceRequest);

        ResponseValidator.verifyNotFound(CalendarOccurrenceActions.getDeleteOccurrenceResponse(getServerPort(), sharedWithToken, eventId, occurrenceId));
    }

    @DataProvider(parallel = true)
    public static Object[][] deleteSharedOccurrenceData() {
        return new Object[][]{
            new Object[]{Set.of(), Set.of(), Set.of(Grant.VIEW, Grant.DELETE)},
            new Object[]{Set.of(), Set.of(Grant.DELETE_CHILDREN), Set.of(Grant.VIEW)},
            new Object[]{Set.of(Grant.DELETE_CHILDREN), Set.of(), Set.of(Grant.VIEW)},
        };
    }
}
