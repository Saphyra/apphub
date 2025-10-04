package com.github.saphyra.apphub.integration.backend.calendar.event;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EventWithLabelTest extends BackEndTest {
    private static final String LABEL_1 = "label-1";
    private static final String EVENT_WITHOUT_LABEL_TITLE = "event-without-label";
    private static final String LABEL_2 = "label-2";
    private static final String EVENT_WITH_LABEL_TITLE = "event-with-label";

    @Test(groups = {"be", "calendar"})
    public void eventWithLabel() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID labelId1 = CalendarLabelActions.createLabel(getServerPort(), accessTokenId, LABEL_1);
        UUID labelId2 = CalendarLabelActions.createLabel(getServerPort(), accessTokenId, LABEL_2);
        createEvent(accessTokenId, EVENT_WITHOUT_LABEL_TITLE, List.of());
        UUID eventWithLabel = createEvent(accessTokenId, EVENT_WITH_LABEL_TITLE, List.of(labelId1));

        CustomAssertions.singleListAssertThat(CalendarEventActions.getEvents(getServerPort(), accessTokenId, labelId1))
            .returns(EVENT_WITH_LABEL_TITLE, EventResponse::getTitle)
            .returns(List.of(labelId1), EventResponse::getLabels);

        editEvent(accessTokenId, eventWithLabel, List.of(labelId2));

        assertThat(CalendarEventActions.getEvents(getServerPort(), accessTokenId, labelId1)).isEmpty();

        CustomAssertions.singleListAssertThat(CalendarEventActions.getEvents(getServerPort(), accessTokenId, labelId2))
            .returns(List.of(labelId2), EventResponse::getLabels);

        assertThat(CalendarEventActions.getEvents(getServerPort(), accessTokenId)).hasSize(2);

        editEvent(accessTokenId, eventWithLabel, List.of());

        assertThat(CalendarEventActions.getEvents(getServerPort(), accessTokenId, labelId1)).isEmpty();
        assertThat(CalendarEventActions.getEvents(getServerPort(), accessTokenId, labelId2)).isEmpty();
        assertThat(CalendarEventActions.getEvents(getServerPort(), accessTokenId)).hasSize(2);
    }

    private void editEvent(UUID accessTokenId, UUID eventWithLabel, List<UUID> labelIds) {
        EventRequest request = EventRequestFactory.editRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(labelIds)
            .build();

        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventWithLabel, request);
    }

    private UUID createEvent(UUID accessTokenId, String eventWithoutLabelTitle, List<UUID> labelIds) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .title(eventWithoutLabelTitle)
            .labels(labelIds)
            .build();

        return CalendarEventActions.createEvent(getServerPort(), accessTokenId, request);
    }
}
