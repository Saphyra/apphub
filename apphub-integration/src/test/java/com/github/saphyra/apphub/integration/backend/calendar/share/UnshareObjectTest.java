package com.github.saphyra.apphub.integration.backend.calendar.share;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarShareActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.ShareObjectRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UnshareObjectTest extends BackEndTest {
    private static final String LABEL_1 = "label-1";

    @Test(groups = {"be", "calendar"})
    public void unshareObject() {
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
        ShareObjectRequest shareObjectRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(labelId)
            .parent(ownerId)
            .type(SharedObjectType.LABEL)
            .grants(Set.of(Grant.VIEW))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareObjectRequest);

        notOwnAlm(sharedWithToken, sharedWithUserId, labelId);
        unshare(ownerToken, sharedWithUserId, labelId);
    }

    private void unshare(String ownerToken, UUID sharedWithUserId, UUID labelId) {
        CalendarShareActions.unshareObject(getServerPort(), ownerToken, SharedObjectType.LABEL, labelId, sharedWithUserId);

        assertThat(CalendarShareActions.getSharedObject(getServerPort(), ownerToken, SharedObjectType.LABEL, labelId, null).getSharedWith()).isEmpty();
    }

    private void notOwnAlm(String sharedWithToken, UUID sharedWithUserId, UUID labelId) {
        CalendarShareActions.unshareObject(getServerPort(), sharedWithToken, SharedObjectType.LABEL, labelId, sharedWithUserId);

        assertThat(CalendarShareActions.getSharedObject(getServerPort(), sharedWithToken, SharedObjectType.LABEL, labelId, null).getSharedWith()).isNotEmpty();
    }
}
