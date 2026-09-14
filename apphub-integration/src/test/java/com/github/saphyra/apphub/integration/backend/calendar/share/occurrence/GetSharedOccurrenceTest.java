package com.github.saphyra.apphub.integration.backend.calendar.share.occurrence;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarShareActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.action.backend.calendar.OccurrenceRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceStatus;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.calendar.ShareObjectRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory.DEFAULT_CONTENT;
import static com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory.DEFAULT_TIME;
import static com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory.DEFAULT_TITLE;
import static com.github.saphyra.apphub.integration.framework.Constants.QUESTION_MARK;
import static org.assertj.core.api.Assertions.assertThat;

public class GetSharedOccurrenceTest extends BackEndTest {
    private static final String NOTE = "note";
    private static final String LABEL = "label";

    @Test(groups = {"be", "calendar"}, dataProvider = "getSharedOccurrenceData")
    void getSharedOccurrence(
        Set<Grant> labelGrants,
        Set<Grant> eventGrants,
        Set<Grant> occurrenceGrants,
        String expectedTitle,
        String expectedContent,
        LocalTime expectedTime,
        String expectedNote,
        Integer expectedRemindMeBeforeDays,
        Boolean expectedReminded,
        Boolean expectedEventArchived,
        Boolean expectedAutoDone
    ) {
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
        OccurrenceRequest occurrenceRequest = OccurrenceRequestFactory.fromResponse(occurrenceResponse)
            .note(NOTE)
            .build();
        CalendarOccurrenceActions.editOccurrence(getServerPort(), ownerToken, eventId, occurrenceId, occurrenceRequest);

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

        //Query
        OccurrenceResponse sharedOccurrenceResponse = CalendarOccurrenceActions.getOccurrence(getServerPort(), sharedWithToken, eventId, occurrenceId);

        assertThat(sharedOccurrenceResponse)
            .returns(occurrenceId, OccurrenceResponse::getOccurrenceId)
            .returns(eventId, OccurrenceResponse::getEventId)
            .returns(eventRequest.getStartDate(), OccurrenceResponse::getDate)
            .returns(expectedTime, OccurrenceResponse::getTime)
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus)
            .returns(expectedTitle, OccurrenceResponse::getTitle)
            .returns(expectedContent, OccurrenceResponse::getContent)
            .returns(expectedNote, OccurrenceResponse::getNote)
            .returns(expectedRemindMeBeforeDays, OccurrenceResponse::getRemindMeBeforeDays)
            .returns(expectedReminded, OccurrenceResponse::getReminded)
            .returns(expectedEventArchived, OccurrenceResponse::getEventArchived)
            .returns(expectedAutoDone, OccurrenceResponse::getAutoDone)
            .returns(true, OccurrenceResponse::getShared);
    }

    @DataProvider(parallel = true)
    public static Object[][] getSharedOccurrenceData() {
        return new Object[][]{
            //Share occurrence
            new Object[]{Set.of(), Set.of(), Set.of(Grant.VIEW), QUESTION_MARK, "", null, NOTE, 0, false, false, null},
            new Object[]{Set.of(), Set.of(), Set.of(Grant.SEE), QUESTION_MARK, "", null, "", 0, false, false, null},

            //Share event
            new Object[]{Set.of(), Set.of(Grant.VIEW, Grant.VIEW_CHILDREN), Set.of(), DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_TIME, NOTE, 0, false, false, false},
            new Object[]{Set.of(), Set.of(Grant.VIEW, Grant.SEE_CHILDREN), Set.of(), DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_TIME, "", 0, false, false, false},
            new Object[]{Set.of(), Set.of(Grant.SEE, Grant.VIEW_CHILDREN), Set.of(), QUESTION_MARK, "", null, NOTE, 0, false, false, null},
            new Object[]{Set.of(), Set.of(Grant.SEE, Grant.SEE_CHILDREN), Set.of(), QUESTION_MARK, "", null, "", 0, false, false, null},

            //Share event and occurrence
            new Object[]{Set.of(), Set.of(Grant.VIEW), Set.of(Grant.VIEW), DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_TIME, NOTE, 0, false, false, false},
            new Object[]{Set.of(), Set.of(Grant.VIEW), Set.of(Grant.SEE), DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_TIME, "", 0, false, false, false},
            new Object[]{Set.of(), Set.of(Grant.VIEW, Grant.VIEW_CHILDREN), Set.of(Grant.SEE), DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_TIME, NOTE, 0, false, false, false},
            new Object[]{Set.of(), Set.of(Grant.VIEW, Grant.SEE_CHILDREN), Set.of(Grant.VIEW), DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_TIME, NOTE, 0, false, false, false},
            new Object[]{Set.of(), Set.of(Grant.SEE), Set.of(Grant.VIEW), QUESTION_MARK, "", null, NOTE, 0, false, false, null},
            new Object[]{Set.of(), Set.of(Grant.SEE), Set.of(Grant.SEE), QUESTION_MARK, "", null, "", 0, false, false, null},

            //Share label
            new Object[]{Set.of(Grant.VIEW_CHILDREN), Set.of(), Set.of(), DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_TIME, NOTE, 0, false, false, false},
            new Object[]{Set.of(Grant.SEE_CHILDREN), Set.of(), Set.of(), QUESTION_MARK, "", null, "", 0, false, false, null},

            //Share label and occurrence
            new Object[]{Set.of(Grant.SEE_CHILDREN), Set.of(), Set.of(Grant.VIEW), QUESTION_MARK, "", null, NOTE, 0, false, false, null},

            //Share label and event
            new Object[]{Set.of(Grant.SEE_CHILDREN), Set.of(Grant.VIEW), Set.of(), DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_TIME, "", 0, false, false, false},
        };
    }
}
