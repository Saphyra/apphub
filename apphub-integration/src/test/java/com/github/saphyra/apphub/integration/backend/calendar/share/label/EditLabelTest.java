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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EditLabelTest extends BackEndTest {
    private static final String LABEL_1 = "label-1";
    private static final String LABEL_2 = "label-2";

    @Test(groups = {"be", "calendar"})
    public void editLabel() {
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
            .grants(Set.of(Grant.EDIT, Grant.VIEW))
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareLabelRequest);

        //Edit label
        CalendarLabelActions.editLabel(getServerPort(), sharedWithToken, labelId, LABEL_2);

        //Verify
        assertThat(CalendarLabelActions.getLabel(getServerPort(), ownerToken, labelId))
            .returns(LABEL_2, LabelResponse::getLabel);
    }

    @Test(groups = {"be", "calendar"}, dataProvider = "editLabel_noGrantData")
    public void editLabel_noGrant(Set<Grant> grants) {
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
            .grants(grants)
            .build();
        CalendarShareActions.shareObject(getServerPort(), ownerToken, shareLabelRequest);

        //Edit label
        ResponseValidator.verifyNotFound(CalendarLabelActions.getEditLabelResponse(getServerPort(), sharedWithToken, labelId, LABEL_2));

        //Verify label is not edited
        assertThat(CalendarLabelActions.getLabel(getServerPort(), ownerToken, labelId))
            .returns(LABEL_1, LabelResponse::getLabel);
    }

    @DataProvider(parallel = true)
    public static Object[][] editLabel_noGrantData() {
        return new Object[][]{
            {Set.of(Grant.VIEW)},
            {Set.of(Grant.EDIT)},
        };
    }
}
