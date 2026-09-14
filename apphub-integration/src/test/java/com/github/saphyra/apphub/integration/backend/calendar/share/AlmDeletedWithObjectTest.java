package com.github.saphyra.apphub.integration.backend.calendar.share;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarShareActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.CalendarDynamoDbRepository;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.LabelResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.calendar.ShareObjectRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.EnumSet;
import java.util.UUID;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

public class AlmDeletedWithObjectTest extends BackEndTest {
    @Test(groups = {"be", "calendar"}, dataProvider = "deletedByData")
    public void almDeletedWithLabel(BiFunction<String, String, String> deletedByTokenSelector) {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        UUID labelId = CalendarLabelActions.createLabel(getServerPort(), ownerToken, "label")
            .getLabelId();

        shareObject(ownerToken, sharedWithId, ownerId, labelId, ownerId, SharedObjectType.LABEL);

        assertThat(CalendarLabelActions.getLabels(getServerPort(), sharedWithToken)).extracting(LabelResponse::getLabelId).contains(labelId);
        assertThat(CalendarDynamoDbRepository.almRecordExists(sharedWithId, SharedObjectType.LABEL, labelId)).isTrue();

        CalendarLabelActions.deleteLabel(getServerPort(), deletedByTokenSelector.apply(ownerToken, sharedWithToken), labelId);

        assertThat(CalendarLabelActions.getLabels(getServerPort(), ownerToken)).isEmpty();
        assertThat(CalendarLabelActions.getLabels(getServerPort(), sharedWithToken)).isEmpty();
        ResponseValidator.verifyNotFound(CalendarShareActions.getGetSharedObjectResponse(getServerPort(), ownerToken, SharedObjectType.LABEL, labelId, null));
        ResponseValidator.verifyNotFound(CalendarShareActions.getGetSharedObjectResponse(getServerPort(), sharedWithToken, SharedObjectType.LABEL, labelId, null));
        assertThat(CalendarDynamoDbRepository.almRecordExists(sharedWithId, SharedObjectType.LABEL, labelId)).isFalse();
    }

    @Test(groups = {"be", "calendar"}, dataProvider = "deletedByData")
    public void almDeletedWithEvent(BiFunction<String, String, String> deletedByTokenSelector) {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, EventRequestFactory.validRequest(RepetitionType.ONE_TIME));

        shareObject(ownerToken, sharedWithId, ownerId, eventId, ownerId, SharedObjectType.EVENT);

        assertThat(CalendarEventActions.getEvents(getServerPort(), sharedWithToken)).extracting(EventResponse::getEventId).contains(eventId);
        assertThat(CalendarDynamoDbRepository.almRecordExists(sharedWithId, SharedObjectType.EVENT, eventId)).isTrue();

        CalendarEventActions.deleteEvent(getServerPort(), deletedByTokenSelector.apply(ownerToken, sharedWithToken), eventId);

        assertThat(CalendarEventActions.getEvents(getServerPort(), ownerToken)).isEmpty();
        assertThat(CalendarEventActions.getEvents(getServerPort(), sharedWithToken)).isEmpty();
        ResponseValidator.verifyNotFound(CalendarShareActions.getGetSharedObjectResponse(getServerPort(), ownerToken, SharedObjectType.EVENT, eventId, null));
        ResponseValidator.verifyNotFound(CalendarShareActions.getGetSharedObjectResponse(getServerPort(), sharedWithToken, SharedObjectType.EVENT, eventId, null));
        assertThat(CalendarDynamoDbRepository.almRecordExists(sharedWithId, SharedObjectType.EVENT, eventId)).isFalse();
    }

    @Test(groups = {"be", "calendar"}, dataProvider = "deletedByData")
    public void almDeletedWithOccurrence(BiFunction<String, String, String> deletedByTokenSelector) {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, EventRequestFactory.validRequest(RepetitionType.ONE_TIME));
        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)
            .stream()
            .map(OccurrenceResponse::getOccurrenceId)
            .findFirst()
            .orElseThrow();

        shareObject(ownerToken, sharedWithId, ownerId, occurrenceId, eventId, SharedObjectType.OCCURRENCE);

        assertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), sharedWithToken, eventId)).extracting(OccurrenceResponse::getOccurrenceId).contains(occurrenceId);
        assertThat(CalendarDynamoDbRepository.almRecordExists(sharedWithId, SharedObjectType.OCCURRENCE, occurrenceId)).isTrue();

        CalendarOccurrenceActions.deleteOccurrence(getServerPort(), deletedByTokenSelector.apply(ownerToken, sharedWithToken), eventId, occurrenceId);

        assertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)).isEmpty();
        assertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), sharedWithToken, eventId)).isEmpty();
        ResponseValidator.verifyNotFound(CalendarShareActions.getGetSharedObjectResponse(getServerPort(), ownerToken, SharedObjectType.OCCURRENCE, occurrenceId, eventId));
        ResponseValidator.verifyNotFound(CalendarShareActions.getGetSharedObjectResponse(getServerPort(), sharedWithToken, SharedObjectType.OCCURRENCE, occurrenceId, eventId));
        assertThat(CalendarDynamoDbRepository.almRecordExists(sharedWithId, SharedObjectType.OCCURRENCE, occurrenceId)).isFalse();
    }

    @DataProvider(parallel = true)
    public Object[][] deletedByData() {
        return new Object[][]{
            new Object[]{(BiFunction<String, String, String>) (ownerToken, ignored) -> ownerToken},
            new Object[]{(BiFunction<String, String, String>) (ignored, sharedWithToken) -> sharedWithToken}
        };
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
                .grants(EnumSet.allOf(Grant.class))
                .build()
        );
    }
}
