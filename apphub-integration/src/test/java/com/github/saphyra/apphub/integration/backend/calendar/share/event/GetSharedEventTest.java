package com.github.saphyra.apphub.integration.backend.calendar.share.event;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarShareActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.calendar.ShareObjectRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory.DEFAULT_CONTENT;
import static com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory.DEFAULT_TITLE;
import static com.github.saphyra.apphub.integration.framework.Constants.QUESTION_MARK;
import static org.assertj.core.api.Assertions.assertThat;

public class GetSharedEventTest extends BackEndTest {
    private static final String LABEL = "label";

    @Test(groups = {"be", "calendar"}, dataProvider = "getSharedEventData")
    public void getSharedEvent(
        Set<Grant> labelGrants,
        Set<Grant> eventGrants,
        String expectedTitle,
        String expectedContent,
        Integer expectedRemindMeBeforeDays,
        Boolean expectedArchived,
        Boolean expectedAutoDone,
        int expectedLabelSize
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

        EventResponse event = CalendarEventActions.getEvent(getServerPort(), sharedWithToken, eventId);
        assertThat(event)
            .returns(expectedTitle, EventResponse::getTitle)
            .returns(expectedContent, EventResponse::getContent)
            .returns(expectedRemindMeBeforeDays, EventResponse::getRemindMeBeforeDays)
            .returns(expectedArchived, EventResponse::getArchived)
            .returns(expectedAutoDone, EventResponse::getAutoDone)
            .returns(true, EventResponse::getShared);

        assertThat(event.getLabels()).hasSize(expectedLabelSize);
    }

    @DataProvider
    public static Object[][] getSharedEventData() {
        return new Object[][]{
            new Object[]{Set.of(), Set.of(Grant.VIEW), DEFAULT_TITLE, DEFAULT_CONTENT, 0, false, false, 1},
            new Object[]{Set.of(), Set.of(Grant.SEE), QUESTION_MARK, "", null, null, null, 0},

            new Object[]{Set.of(Grant.VIEW_CHILDREN), Set.of(), DEFAULT_TITLE, DEFAULT_CONTENT, 0, false, false, 1},
            new Object[]{Set.of(Grant.SEE_CHILDREN), Set.of(), QUESTION_MARK, "", null, null, null, 0},
            new Object[]{Set.of(Grant.SEE_CHILDREN), Set.of(Grant.VIEW), DEFAULT_TITLE, DEFAULT_CONTENT, 0, false, false, 1},
        };
    }
}
