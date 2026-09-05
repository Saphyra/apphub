package com.github.saphyra.apphub.integration.backend.calendar.share.event;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarShareActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.calendar.ShareObjectRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateEventTest extends BackEndTest {
    private static final String LABEL_1 = "label-1";

    @Test(groups = {"be", "calendar"})
    public void createEventToSharedLabel() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create label
        UUID labelId = CalendarLabelActions.createLabel(getServerPort(), ownerToken, LABEL_1)
            .getLabelId();

        //Share label
        ShareObjectRequest shareLabelRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(labelId)
            .parent(ownerId)
            .type(SharedObjectType.LABEL)
            .grants(Set.of(Grant.VIEW, Grant.VIEW_CHILDREN))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareLabelRequest);

        //Create event
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(Map.of(labelId, ownerId))
            .build();
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), sharedWithToken, createEventRequest);

        //Get event by labelId
        assertThat(CalendarEventActions.getEventsOfLabel(getServerPort(), sharedWithToken, labelId))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);

        assertThat(CalendarEventActions.getEventsOfLabel(getServerPort(), ownerToken, labelId))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);

        //Get occurrence of event
        assertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), sharedWithToken, eventId))
            .singleElement()
            .returns(createEventRequest.getTitle(), OccurrenceResponse::getTitle);

        assertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId))
            .singleElement()
            .returns(createEventRequest.getTitle(), OccurrenceResponse::getTitle);

        //Get occurrences in time range
        assertThat(CalendarOccurrenceActions.getOccurrences(getServerPort(), sharedWithToken, createEventRequest.getStartDate().minusDays(1), createEventRequest.getStartDate().plusDays(1)))
            .singleElement()
            .returns(createEventRequest.getTitle(), OccurrenceResponse::getTitle);

        assertThat(CalendarOccurrenceActions.getOccurrences(getServerPort(), ownerToken, createEventRequest.getStartDate().minusDays(1), createEventRequest.getStartDate().plusDays(1)))
            .singleElement()
            .returns(createEventRequest.getTitle(), OccurrenceResponse::getTitle);
    }
}
