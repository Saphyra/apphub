package com.github.saphyra.apphub.integration.backend.calendar.share;

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
import com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.calendar.ShareObjectRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchSharedObjectTest extends BackEndTest {
    private static final String LABEL = "label";

    /**
     * Tests searching events of a shared label with view children permission.
     * <ul>
     *   <li>Creates a label and shares it.</li>
     *   <li>Creates an event under the label.</li>
     *   <li>Searches for the event as owner and verifies result.</li>
     *   <li>Searches as shared user and verifies result.</li>
     * </ul>
     */
    @Test(groups = {"be", "calendar"})
    public void searchEventOfSharedLabel_viewChildren() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create label
        UUID labelId = CalendarLabelActions.createLabel(getServerPort(), ownerToken, LABEL)
            .getLabelId();

        //Share label
        ShareObjectRequest shareObjectRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(labelId)
            .parent(ownerId)
            .type(SharedObjectType.LABEL)
            .grants(Set.of(Grant.VIEW, Grant.VIEW_CHILDREN))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareObjectRequest);

        //Create event
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(Map.of(labelId, ownerId))
            .build();
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, createEventRequest);

        //Search
        assertThat(CalendarEventActions.search(getServerPort(), ownerToken, createEventRequest.getTitle()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);

        assertThat(CalendarEventActions.search(getServerPort(), sharedWithToken, createEventRequest.getTitle()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);
    }

    /**
     * Tests searching events of a shared label with see children permission.
     * <ul>
     *   <li>Creates a label and shares it with SEE_CHILDREN.</li>
     *   <li>Creates an event under the label.</li>
     *   <li>Searches for the event as owner and verifies result.</li>
     *   <li>Searches as shared user expecting no results.</li>
     * </ul>
     */
    @Test(groups = {"be", "calendar"})
    public void searchEventOfSharedLabel_seeChildren() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create label
        UUID labelId = CalendarLabelActions.createLabel(getServerPort(), ownerToken, LABEL)
            .getLabelId();

        //Share label
        ShareObjectRequest shareObjectRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(labelId)
            .parent(ownerId)
            .type(SharedObjectType.LABEL)
            .grants(Set.of(Grant.VIEW, Grant.SEE_CHILDREN))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareObjectRequest);

        //Create event
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(Map.of(labelId, ownerId))
            .build();
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, createEventRequest);

        //Search
        assertThat(CalendarEventActions.search(getServerPort(), ownerToken, createEventRequest.getTitle()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);

        assertThat(CalendarEventActions.search(getServerPort(), sharedWithToken, createEventRequest.getTitle())).isEmpty();
    }

    /**
     * Tests searching a shared event with view permission.
     * <ul>
     *   <li>Creates an event and shares it.</li>
     *   <li>Searches for the event as owner and verifies result.</li>
     *   <li>Searches as shared user and verifies result.</li>
     * </ul>
     */
    @Test(groups = {"be", "calendar"})
    public void searchSharedEvent_view() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create event
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME);
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, createEventRequest);

        //Share event
        ShareObjectRequest shareObjectRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(eventId)
            .parent(ownerId)
            .type(SharedObjectType.EVENT)
            .grants(Set.of(Grant.VIEW))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareObjectRequest);

        //Search
        assertThat(CalendarEventActions.search(getServerPort(), ownerToken, createEventRequest.getTitle()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);

        assertThat(CalendarEventActions.search(getServerPort(), sharedWithToken, createEventRequest.getTitle()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);
    }

    /**
     * Tests searching a shared event with see permission.
     * <ul>
     *   <li>Creates an event and shares it with SEE.</li>
     *   <li>Searches for the event as owner and verifies result.</li>
     *   <li>Searches as shared user expecting no results.</li>
     * </ul>
     */
    @Test(groups = {"be", "calendar"})
    public void searchSharedEvent_see() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create event
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME);
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, createEventRequest);

        //Share event
        ShareObjectRequest shareObjectRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(eventId)
            .parent(ownerId)
            .type(SharedObjectType.EVENT)
            .grants(Set.of(Grant.SEE))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareObjectRequest);

        //Search
        assertThat(CalendarEventActions.search(getServerPort(), ownerToken, createEventRequest.getTitle()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);

        assertThat(CalendarEventActions.search(getServerPort(), sharedWithToken, createEventRequest.getTitle())).isEmpty();
    }

    /**
     * Tests searching occurrences of a shared label with view children permission.
     * <ul>
     *   <li>Creates a label, shares it, creates an event and occurrence.</li>
     *   <li>Edits the occurrence.</li>
     *   <li>Searches for the occurrence as owner and verifies result.</li>
     *   <li>Searches as shared user and verifies result.</li>
     * </ul>
     */
    @Test(groups = {"be", "calendar"})
    public void searchOccurrenceOfSharedLabel_viewChildren() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create label
        UUID labelId = CalendarLabelActions.createLabel(getServerPort(), ownerToken, LABEL)
            .getLabelId();

        //Share label
        ShareObjectRequest shareObjectRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(labelId)
            .parent(ownerId)
            .type(SharedObjectType.LABEL)
            .grants(Set.of(Grant.VIEW, Grant.VIEW_CHILDREN))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareObjectRequest);

        //Create event
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(Map.of(labelId, ownerId))
            .build();
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, createEventRequest);

        //Edit occurrence
        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)
            .getFirst()
            .getOccurrenceId();
        OccurrenceRequest editOccurrenceRequest = OccurrenceRequestFactory.validRequest();

        CalendarOccurrenceActions.editOccurrence(getServerPort(), ownerToken, eventId, occurrenceId, editOccurrenceRequest);

        //Search
        assertThat(CalendarEventActions.search(getServerPort(), ownerToken, editOccurrenceRequest.getNote()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);

        assertThat(CalendarEventActions.search(getServerPort(), sharedWithToken, editOccurrenceRequest.getNote()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);
    }

    /**
     * Tests searching occurrences of a shared label with see children permission.
     * <ul>
     *   <li>Creates a label, shares it with SEE_CHILDREN, creates an event and occurrence.</li>
     *   <li>Edits the occurrence.</li>
     *   <li>Searches for the occurrence as owner and verifies result.</li>
     *   <li>Searches as shared user expecting no results.</li>
     * </ul>
     */
    @Test(groups = {"be", "calendar"})
    public void searchOccurrenceOfSharedLabel_seeChildren() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create label
        UUID labelId = CalendarLabelActions.createLabel(getServerPort(), ownerToken, LABEL)
            .getLabelId();

        //Share label
        ShareObjectRequest shareObjectRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(labelId)
            .parent(ownerId)
            .type(SharedObjectType.LABEL)
            .grants(Set.of(Grant.VIEW, Grant.SEE_CHILDREN))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareObjectRequest);

        //Create event
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(Map.of(labelId, ownerId))
            .build();
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, createEventRequest);

        //Edit occurrence
        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)
            .getFirst()
            .getOccurrenceId();
        OccurrenceRequest editOccurrenceRequest = OccurrenceRequestFactory.validRequest();

        CalendarOccurrenceActions.editOccurrence(getServerPort(), ownerToken, eventId, occurrenceId, editOccurrenceRequest);

        //Search
        assertThat(CalendarEventActions.search(getServerPort(), ownerToken, editOccurrenceRequest.getNote()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);

        assertThat(CalendarEventActions.search(getServerPort(), sharedWithToken, editOccurrenceRequest.getNote())).isEmpty();
    }

    /**
     * Tests searching occurrences of a shared event with view children permission.
     * <ul>
     *   <li>Creates an event, shares it, creates and edits an occurrence.</li>
     *   <li>Searches for the occurrence as owner and verifies result.</li>
     *   <li>Searches as shared user and verifies result.</li>
     * </ul>
     */
    @Test(groups = {"be", "calendar"})
    public void searchOccurrenceOfSharedEvent_viewChildren() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create event
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME);
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, createEventRequest);

        //Share event
        ShareObjectRequest shareObjectRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(eventId)
            .parent(ownerId)
            .type(SharedObjectType.EVENT)
            .grants(Set.of(Grant.VIEW, Grant.VIEW_CHILDREN))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareObjectRequest);

        //Edit occurrence
        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)
            .getFirst()
            .getOccurrenceId();
        OccurrenceRequest editOccurrenceRequest = OccurrenceRequestFactory.validRequest();

        CalendarOccurrenceActions.editOccurrence(getServerPort(), ownerToken, eventId, occurrenceId, editOccurrenceRequest);

        //Search
        assertThat(CalendarEventActions.search(getServerPort(), ownerToken, editOccurrenceRequest.getNote()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);

        assertThat(CalendarEventActions.search(getServerPort(), sharedWithToken, editOccurrenceRequest.getNote()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);
    }

    /**
     * Tests searching occurrences of a shared event with see children permission.
     * <ul>
     *   <li>Creates an event, shares it with SEE_CHILDREN, creates and edits an occurrence.</li>
     *   <li>Searches for the occurrence as owner and verifies result.</li>
     *   <li>Searches as shared user expecting no results.</li>
     * </ul>
     */
    @Test(groups = {"be", "calendar"})
    public void searchOccurrenceOfSharedEvent_seeChildren() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create event
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME);
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, createEventRequest);

        //Share event
        ShareObjectRequest shareObjectRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(eventId)
            .parent(ownerId)
            .type(SharedObjectType.EVENT)
            .grants(Set.of(Grant.VIEW, Grant.SEE_CHILDREN))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareObjectRequest);

        //Edit occurrence
        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)
            .getFirst()
            .getOccurrenceId();
        OccurrenceRequest editOccurrenceRequest = OccurrenceRequestFactory.validRequest();

        CalendarOccurrenceActions.editOccurrence(getServerPort(), ownerToken, eventId, occurrenceId, editOccurrenceRequest);

        //Search
        assertThat(CalendarEventActions.search(getServerPort(), ownerToken, editOccurrenceRequest.getNote()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);

        assertThat(CalendarEventActions.search(getServerPort(), sharedWithToken, editOccurrenceRequest.getNote())).isEmpty();
    }

    /**
     * Tests searching a shared occurrence with view permission.
     * <ul>
     *   <li>Creates an event, creates and edits an occurrence.</li>
     *   <li>Shares the occurrence with VIEW.</li>
     *   <li>Searches for the occurrence as owner and verifies result.</li>
     *   <li>Searches as shared user and verifies result.</li>
     * </ul>
     */
    @Test(groups = {"be", "calendar"})
    public void searchSharedOccurrence_view() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create event
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME);
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, createEventRequest);

        //Edit occurrence
        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)
            .getFirst()
            .getOccurrenceId();
        OccurrenceRequest editOccurrenceRequest = OccurrenceRequestFactory.validRequest();

        CalendarOccurrenceActions.editOccurrence(getServerPort(), ownerToken, eventId, occurrenceId, editOccurrenceRequest);

        //Share occurrence
        ShareObjectRequest shareObjectRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(occurrenceId)
            .parent(eventId)
            .type(SharedObjectType.OCCURRENCE)
            .grants(Set.of(Grant.VIEW))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareObjectRequest);

        //Search
        assertThat(CalendarEventActions.search(getServerPort(), ownerToken, editOccurrenceRequest.getNote()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);

        assertThat(CalendarEventActions.search(getServerPort(), sharedWithToken, editOccurrenceRequest.getNote()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);
    }

    /**
     * Tests searching a shared occurrence with see permission.
     * <ul>
     *   <li>Creates an event, creates and edits an occurrence.</li>
     *   <li>Shares the occurrence with SEE.</li>
     *   <li>Searches for the occurrence as owner and verifies result.</li>
     *   <li>Searches as shared user expecting no results.</li>
     * </ul>
     */
    @Test(groups = {"be", "calendar"})
    public void searchSharedOccurrence_see() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create event
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME);
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, createEventRequest);

        //Edit occurrence
        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)
            .getFirst()
            .getOccurrenceId();
        OccurrenceRequest editOccurrenceRequest = OccurrenceRequestFactory.validRequest();

        CalendarOccurrenceActions.editOccurrence(getServerPort(), ownerToken, eventId, occurrenceId, editOccurrenceRequest);

        //Share occurrence
        ShareObjectRequest shareObjectRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(occurrenceId)
            .parent(eventId)
            .type(SharedObjectType.OCCURRENCE)
            .grants(Set.of(Grant.SEE))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareObjectRequest);

        //Search
        assertThat(CalendarEventActions.search(getServerPort(), ownerToken, editOccurrenceRequest.getNote()))
            .singleElement()
            .returns(eventId, EventResponse::getEventId);

        assertThat(CalendarEventActions.search(getServerPort(), sharedWithToken, editOccurrenceRequest.getNote())).isEmpty();
    }
}
