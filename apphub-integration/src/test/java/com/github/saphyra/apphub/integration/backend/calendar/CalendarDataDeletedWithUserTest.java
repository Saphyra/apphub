package com.github.saphyra.apphub.integration.backend.calendar;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarShareActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.CalendarDynamoDbRepository;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.LabelResponse;
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

public class CalendarDataDeletedWithUserTest extends BackEndTest {
    private static final String LABEL = "label";

    @Test(groups = {"be", "calendar"})
    public void calendarDataDeletedWithUser() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        LabelResponse label = CalendarLabelActions.createLabel(getServerPort(), ownerToken, LABEL);

        EventRequest eventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(Map.of(label.getLabelId(), label.getUserId()))
            .build();
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, eventRequest);
        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)
            .stream()
            .map(OccurrenceResponse::getOccurrenceId)
            .findFirst()
            .orElseThrow();

        shareObject(ownerToken, sharedWithId, ownerId, label.getLabelId(), ownerId, SharedObjectType.LABEL);
        shareObject(ownerToken, sharedWithId, ownerId, eventId, ownerId, SharedObjectType.EVENT);
        shareObject(ownerToken, sharedWithId, ownerId, occurrenceId, eventId, SharedObjectType.OCCURRENCE);

        assertThat(CalendarLabelActions.getLabels(getServerPort(), sharedWithToken)).extracting(LabelResponse::getLabelId).contains(label.getLabelId());
        assertThat(CalendarEventActions.getEvents(getServerPort(), sharedWithToken)).extracting(com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse::getEventId).contains(eventId);
        assertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), sharedWithToken, eventId)).extracting(OccurrenceResponse::getOccurrenceId).contains(occurrenceId);

        assertThat(CalendarDynamoDbRepository.almRecordExists(sharedWithId, SharedObjectType.LABEL, label.getLabelId())).isTrue();
        assertThat(CalendarDynamoDbRepository.almRecordExists(sharedWithId, SharedObjectType.EVENT, eventId)).isTrue();
        assertThat(CalendarDynamoDbRepository.almRecordExists(sharedWithId, SharedObjectType.OCCURRENCE, occurrenceId)).isTrue();

        AccountActions.deleteAccount(getServerPort(), ownerToken, ownerData.getPassword());

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarDynamoDbRepository.calendarRecordExists(ownerId)).isFalse();
            assertThat(CalendarDynamoDbRepository.occurrenceExists(eventId)).isFalse();
            assertThat(CalendarDynamoDbRepository.almRecordExists(sharedWithId, SharedObjectType.LABEL, label.getLabelId())).isFalse();
            assertThat(CalendarDynamoDbRepository.almRecordExists(sharedWithId, SharedObjectType.EVENT, eventId)).isFalse();
            assertThat(CalendarDynamoDbRepository.almRecordExists(sharedWithId, SharedObjectType.OCCURRENCE, occurrenceId)).isFalse();
        });
    }

    private void shareObject(String ownerToken, UUID sharedWithId, UUID ownerId, UUID objectId, UUID parent, SharedObjectType type) {
        CalendarShareActions.shareObject(
            getServerPort(),
            ownerToken,
            ShareObjectRequest.builder()
                .sharedWith(sharedWithId)
                .owner(ownerId)
                .objectId(objectId)
                .parent(parent)
                .type(type)
                .grants(Set.of(Grant.VIEW))
                .build()
        );
    }
}
