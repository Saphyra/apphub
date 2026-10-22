package com.github.saphyra.apphub.integration.backend.calendar;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.ValidationConstants;
import com.github.saphyra.apphub.integration.structure.api.calendar.LabelResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarLabelsTest extends BackEndTest {
    private static final String LABEL = "label";
    private static final String NEW_LABEL = "new-label";

    @Test(groups = {"be", "calendar"})
    public void labelCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_blank(accessToken);
        create_tooLong(accessToken);
        LabelResponse label = create(accessToken);

        getLabels(accessToken, label.getLabelId());

        edit_blank(accessToken, label.getLabelId());
        edit_tooLong(accessToken, label.getLabelId());
        edit(accessToken, label.getLabelId());

        getLabel(accessToken, label.getLabelId());

        delete(accessToken, label.getLabelId());
    }

    private void delete(String accessToken, UUID labelId) {
        assertThat(CalendarLabelActions.deleteLabel(getServerPort(), accessToken, labelId)).isEmpty();
    }

    private void edit(String accessToken, UUID labelId) {
        CustomAssertions.singleListAssertThat(CalendarLabelActions.editLabel(getServerPort(), accessToken, labelId, NEW_LABEL))
            .returns(labelId, LabelResponse::getLabelId)
            .returns(NEW_LABEL, LabelResponse::getLabel);
    }

    private void edit_tooLong(String accessToken, UUID labelId) {
        String label = "a".repeat(256);
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getEditLabelResponse(getServerPort(), accessToken, labelId, label), "label", ValidationConstants.TOO_LONG);
    }

    private void edit_blank(String accessToken, UUID labelId) {
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getEditLabelResponse(getServerPort(), accessToken, labelId, " "), "label", ValidationConstants.MUST_NOT_BE_NULL_OR_BLANK);
    }

    private void getLabel(String accessToken, UUID labelId) {
        assertThat(CalendarLabelActions.getLabel(getServerPort(), accessToken, labelId))
            .returns(NEW_LABEL, LabelResponse::getLabel).
            returns(labelId, LabelResponse::getLabelId);
    }

    private void getLabels(String accessToken, UUID labelId) {
        CustomAssertions.singleListAssertThat(CalendarLabelActions.getLabels(getServerPort(), accessToken))
            .returns(labelId, LabelResponse::getLabelId)
            .returns(LABEL, LabelResponse::getLabel);
    }

    private LabelResponse create(String accessToken) {
        LabelResponse label = CalendarLabelActions.createLabel(getServerPort(), accessToken, LABEL);
        assertThat(label).isNotNull();

        return label;
    }

    private void create_tooLong(String accessToken) {
        String label = "a".repeat(256);
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getCreateLabelResponse(getServerPort(), accessToken, label), "label", ValidationConstants.TOO_LONG);
    }

    private void create_blank(String accessToken) {
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getCreateLabelResponse(getServerPort(), accessToken, " "), "label", ValidationConstants.MUST_NOT_BE_NULL_OR_BLANK);
    }
}
