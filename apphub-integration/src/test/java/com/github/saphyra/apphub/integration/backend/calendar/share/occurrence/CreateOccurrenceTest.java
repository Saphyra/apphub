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

import static com.github.saphyra.apphub.integration.action.backend.calendar.OccurrenceRequestFactory.DEFAULT_NOTE;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateOccurrenceTest extends BackEndTest {
    private static final String LABEL = "label";

    @Test(groups = {"be", "calendar"}, dataProvider = "createOccurrenceData")
    public void createOccurrenceToSharedEvent(Set<Grant> labelGrants, Set<Grant> eventGrants) {
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

        OccurrenceRequest occurrenceRequest = OccurrenceRequestFactory.validRequest();
        UUID newOccurrenceId = CalendarOccurrenceActions.createOccurrence(getServerPort(), sharedWithToken, eventId, occurrenceRequest);

        assertThat(CalendarOccurrenceActions.getOccurrence(getServerPort(), sharedWithToken, eventId, newOccurrenceId))
            .returns(DEFAULT_NOTE, OccurrenceResponse::getNote);

        assertThat(CalendarOccurrenceActions.getOccurrence(getServerPort(), ownerToken, eventId, newOccurrenceId))
            .returns(DEFAULT_NOTE, OccurrenceResponse::getNote);
    }

    @Test(groups = {"be", "calendar"})
    public void createOccurrenceToSharedEvent_noGrant() {
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

        //Share label
        ShareObjectRequest shareLabelRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(labelId)
            .parent(ownerId)
            .type(SharedObjectType.LABEL)
            .grants(Set.of(Grant.VIEW))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareLabelRequest);

        //Share event
        ShareObjectRequest shareEventRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(eventId)
            .parent(ownerId)
            .type(SharedObjectType.EVENT)
            .grants(Set.of(Grant.DELETE))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareEventRequest);

        OccurrenceRequest occurrenceRequest = OccurrenceRequestFactory.validRequest();

        ResponseValidator.verifyNotFound(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), sharedWithToken, eventId, occurrenceRequest));
    }

    @DataProvider(parallel = true)
    public static Object[][] createOccurrenceData() {
        return new Object[][]{
            new Object[]{Set.of(), Set.of(Grant.VIEW, Grant.VIEW_CHILDREN)},
            new Object[]{Set.of(), Set.of(Grant.VIEW, Grant.SEE_CHILDREN)},
            new Object[]{Set.of(), Set.of(Grant.SEE, Grant.VIEW_CHILDREN)},
            new Object[]{Set.of(), Set.of(Grant.SEE, Grant.SEE_CHILDREN)},
            new Object[]{Set.of(Grant.VIEW_CHILDREN), Set.of()},
            new Object[]{Set.of(Grant.SEE_CHILDREN), Set.of()},
        };
    }
}
