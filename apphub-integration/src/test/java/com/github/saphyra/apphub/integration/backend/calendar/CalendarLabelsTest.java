package com.github.saphyra.apphub.integration.backend.calendar;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
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
        UUID labelId = create(accessToken);
        create_alreadyExists(accessToken);

        getLabels(accessToken, labelId);

        edit_blank(accessToken, labelId);
        edit_tooLong(accessToken, labelId);
        edit_alreadyExists(accessToken, labelId);
        edit(accessToken, labelId);

        getLabel(accessToken, labelId);

        delete(accessToken, labelId);
    }

    private void delete(String accessToken, UUID labelId) {
        assertThat(CalendarLabelActions.deleteLabel(getServerPort(), accessToken, labelId)).isEmpty();
    }

    private void edit(String accessToken, UUID labelId) {
        CustomAssertions.singleListAssertThat(CalendarLabelActions.editLabel(getServerPort(), accessToken, labelId, NEW_LABEL))
            .returns(labelId, LabelResponse::getLabelId)
            .returns(NEW_LABEL, LabelResponse::getLabel);
    }

    private void edit_alreadyExists(String accessToken, UUID labelId) {
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getEditLabelResponse(getServerPort(), accessToken, labelId, LABEL), "label", "already exists");
    }

    private void edit_tooLong(String accessToken, UUID labelId) {
        String label = "a".repeat(256);
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getEditLabelResponse(getServerPort(), accessToken, labelId, label), "label", "too long");
    }

    private void edit_blank(String accessToken, UUID labelId) {
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getEditLabelResponse(getServerPort(), accessToken, labelId, " "), "label", "must not be null or blank");
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

    private void create_alreadyExists(String accessToken) {
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getCreateLabelResponse(getServerPort(), accessToken, LABEL), "label", "already exists");
    }

    private UUID create(String accessToken) {
        UUID labelId = CalendarLabelActions.createLabel(getServerPort(), accessToken, LABEL);
        assertThat(labelId).isNotNull();

        return labelId;
    }

    private void create_tooLong(String accessToken) {
        String label = "a".repeat(256);
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getCreateLabelResponse(getServerPort(), accessToken, label), "label", "too long");
    }

    private void create_blank(String accessToken) {
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getCreateLabelResponse(getServerPort(), accessToken, " "), "label", "must not be null or blank");
    }
}
