package com.github.saphyra.apphub.integration.backend.calendar.share.label;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarShareActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.LabelResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.ShareObjectRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetSharedLabelTest extends BackEndTest {
    private static final String LABEL = "label";

    @Test(groups = {"be", "calendar"})
    public void getSharedLabel() {
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
        ShareObjectRequest shareLabelRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(labelId)
            .parent(ownerId)
            .type(SharedObjectType.LABEL)
            .grants(Set.of(Grant.VIEW))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareLabelRequest);

        //Verify label is shared
        assertThat(CalendarLabelActions.getLabels(getServerPort(), sharedWithToken))
            .singleElement()
            .returns(labelId, LabelResponse::getLabelId);
    }

    @Test(groups = {"be", "calendar"})
    public void getSharedLabel_noGrant() {
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
        ShareObjectRequest shareLabelRequest = ShareObjectRequest.builder()
            .sharedWith(sharedWithUserId)
            .owner(ownerId)
            .objectId(labelId)
            .parent(ownerId)
            .type(SharedObjectType.LABEL)
            .grants(Set.of(Grant.EDIT_CHILDREN))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareLabelRequest);

        //Verify label is shared
        ResponseValidator.verifyNotFound(CalendarLabelActions.getGetLabelResponse(getServerPort(), sharedWithToken, labelId));

        assertThat(CalendarLabelActions.getLabels(getServerPort(), sharedWithToken)).isEmpty();
    }
}
