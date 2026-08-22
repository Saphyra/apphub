package com.github.saphyra.apphub.integration.backend.calendar.share;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarShareActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.LabelResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.calendar.ShareObjectRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class ShareObjectTest extends BackEndTest {
    private static final String LABEL_1 = "label-1";
    private static final String LABEL_2 = "label-2";

    @Test(groups = {"be", "calendar"})
    public void shareObject() {
        RegistrationParameters ownerData = RegistrationParameters.validParameters();
        String ownerToken = IndexPageActions.registerAndLogin(getServerPort(), ownerData);
        UUID ownerId = UserDynamoDbRepository.getUserIdByEmail(ownerData.getEmail());

        RegistrationParameters sharedWithData = RegistrationParameters.validParameters();
        String sharedWithToken = IndexPageActions.registerAndLogin(getServerPort(), sharedWithData);
        UUID sharedWithUserId = UserDynamoDbRepository.getUserIdByEmail(sharedWithData.getEmail());

        //Create objects
        UUID ownLabelId = CalendarLabelActions.createLabel(getServerPort(), ownerToken, LABEL_1)
            .getLabelId();
        UUID otherLabelId = CalendarLabelActions.createLabel(getServerPort(), sharedWithToken, LABEL_2)
            .getLabelId();
        EventRequest createEventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME);
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), ownerToken, createEventRequest);
        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), ownerToken, eventId)
            .getFirst()
            .getOccurrenceId();

        Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier = () -> createBuilder(sharedWithUserId, ownerId, ownLabelId, ownerId, SharedObjectType.LABEL, Set.of(Grant.VIEW));

        nullSharedWith(ownerToken, builderSupplier);
        nullOwner(ownerToken, builderSupplier);
        nullObjectId(ownerToken, builderSupplier);
        nullType(ownerToken, builderSupplier);
        nullGrants(ownerToken, builderSupplier);
        emptyGrants(ownerToken, builderSupplier);
        nullParent(ownerToken, builderSupplier);
        notOwnObject(ownerToken, otherLabelId, builderSupplier);
        shareWithSelf(ownerToken, ownerId, builderSupplier);
        sharedWithDoesNotExist(ownerToken, builderSupplier);
        objectDoesNotExist(ownerToken, builderSupplier);
        share(ownerToken, sharedWithToken, builderSupplier, eventId, occurrenceId);
        alreadyShared(ownerToken, builderSupplier);
    }

    private void alreadyShared(String ownerToken, Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier) {
        ShareObjectRequest request = builderSupplier.get()
            .build();

        ResponseValidator.verifyErrorResponse(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, request), 400, ErrorCode.ALREADY_EXISTS);
    }

    private void share(String ownerToken, String sharedWithToken, Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier, UUID eventId, UUID occurrenceId) {
        //Share label
        ShareObjectRequest labelRequest = builderSupplier.get()
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, labelRequest);
        assertThat(CalendarLabelActions.getLabels(getServerPort(), sharedWithToken)).extracting(LabelResponse::getLabelId).contains(labelRequest.getObjectId());

        //Share event
        ShareObjectRequest eventRequest = builderSupplier.get()
            .objectId(eventId)
            .type(SharedObjectType.EVENT)
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, eventRequest);
        assertThat(CalendarEventActions.getEvents(getServerPort(), sharedWithToken)).extracting(EventResponse::getEventId).containsExactly(eventId);

        //Share occurrence
        ShareObjectRequest occurrenceRequest = builderSupplier.get()
            .objectId(occurrenceId)
            .type(SharedObjectType.OCCURRENCE)
            .parent(eventId)
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, occurrenceRequest);
        assertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), sharedWithToken, eventId)).extracting(OccurrenceResponse::getOccurrenceId).containsExactly(occurrenceId);
    }

    private void objectDoesNotExist(String ownerToken, Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier) {
        ShareObjectRequest labelRequest = builderSupplier.get()
            .objectId(UUID.randomUUID())
            .build();
        ResponseValidator.verifyNotFound(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, labelRequest));

        ShareObjectRequest eventRequest = builderSupplier.get()
            .objectId(UUID.randomUUID())
            .type(SharedObjectType.EVENT)
            .build();
        ResponseValidator.verifyNotFound(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, eventRequest));

        ShareObjectRequest occurrenceRequest = builderSupplier.get()
            .objectId(UUID.randomUUID())
            .type(SharedObjectType.OCCURRENCE)
            .build();
        ResponseValidator.verifyNotFound(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, occurrenceRequest));
    }

    private void sharedWithDoesNotExist(String ownerToken, Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier) {
        ShareObjectRequest request = builderSupplier.get()
            .sharedWith(UUID.randomUUID())
            .build();

        ResponseValidator.verifyNotFound(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, request));
    }

    private void shareWithSelf(String ownerToken, UUID ownerId, Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier) {
        ShareObjectRequest request = builderSupplier.get()
            .sharedWith(ownerId)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, request), "sharedWith", "must not be self");
    }

    private void notOwnObject(String ownerToken, UUID otherLabelId, Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier) {
        ShareObjectRequest request = builderSupplier.get()
            .objectId(otherLabelId)
            .build();

        ResponseValidator.verifyNotFound(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, request));
    }

    private void nullParent(String ownerToken, Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier) {
        ShareObjectRequest request = builderSupplier.get()
            .parent(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, request), "parent", "must not be null");
    }

    private void emptyGrants(String ownerToken, Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier) {
        ShareObjectRequest request = builderSupplier.get()
            .grants(Set.of())
            .build();

        ResponseValidator.verifyInvalidParam(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, request), "grants", "must not be empty");
    }

    private void nullGrants(String ownerToken, Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier) {
        ShareObjectRequest request = builderSupplier.get()
            .grants(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, request), "grants", "must not be null");
    }

    private void nullType(String ownerToken, Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier) {
        ShareObjectRequest request = builderSupplier.get()
            .type(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, request), "type", "must not be null");
    }

    private void nullObjectId(String ownerToken, Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier) {
        ShareObjectRequest request = builderSupplier.get()
            .objectId(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, request), "objectId", "must not be null");
    }

    private void nullOwner(String ownerToken, Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier) {
        ShareObjectRequest request = builderSupplier.get()
            .owner(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, request), "owner", "must not be null");
    }

    private void nullSharedWith(String ownerToken, Supplier<ShareObjectRequest.ShareObjectRequestBuilder> builderSupplier) {
        ShareObjectRequest request = builderSupplier.get()
            .sharedWith(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarShareActions.getShareObjectResponse(getServerPort(), ownerToken, request), "sharedWith", "must not be null");
    }

    private ShareObjectRequest.ShareObjectRequestBuilder createBuilder(UUID sharedWith, UUID owner, UUID objectId, UUID parent, SharedObjectType type, Set<Grant> grants) {
        return ShareObjectRequest.builder()
            .sharedWith(sharedWith)
            .owner(owner)
            .objectId(objectId)
            .parent(parent)
            .type(type)
            .grants(grants);
    }
}
