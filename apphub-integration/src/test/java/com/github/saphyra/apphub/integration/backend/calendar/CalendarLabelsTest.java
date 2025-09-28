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
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_blank(accessTokenId);
        create_tooLong(accessTokenId);
        UUID labelId = create(accessTokenId);
        create_alreadyExists(accessTokenId);

        getLabels(accessTokenId, labelId);

        edit_blank(accessTokenId, labelId);
        edit_tooLong(accessTokenId, labelId);
        edit_alreadyExists(accessTokenId, labelId);
        edit(accessTokenId, labelId);

        getLabel(accessTokenId, labelId);

        delete(accessTokenId, labelId);
    }

    private void delete(UUID accessTokenId, UUID labelId) {
        assertThat(CalendarLabelActions.deleteLabel(getServerPort(), accessTokenId, labelId)).isEmpty();
    }

    private void edit(UUID accessTokenId, UUID labelId) {
        CustomAssertions.singleListAssertThat(CalendarLabelActions.editLabel(getServerPort(), accessTokenId, labelId, NEW_LABEL))
            .returns(labelId, LabelResponse::getLabelId)
            .returns(NEW_LABEL, LabelResponse::getLabel);
    }

    private void edit_alreadyExists(UUID accessTokenId, UUID labelId) {
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getEditLabelResponse(getServerPort(), accessTokenId, labelId, LABEL), "label", "already exists");
    }

    private void edit_tooLong(UUID accessTokenId, UUID labelId) {
        String label = "a".repeat(256);
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getEditLabelResponse(getServerPort(), accessTokenId, labelId, label), "label", "too long");
    }

    private void edit_blank(UUID accessTokenId, UUID labelId) {
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getEditLabelResponse(getServerPort(), accessTokenId, labelId, " "), "label", "must not be null or blank");
    }

    private void getLabel(UUID accessTokenId, UUID labelId) {
        assertThat(CalendarLabelActions.getLabel(getServerPort(), accessTokenId, labelId))
            .returns(NEW_LABEL, LabelResponse::getLabel).
            returns(labelId, LabelResponse::getLabelId);
    }

    private void getLabels(UUID accessTokenId, UUID labelId) {
        CustomAssertions.singleListAssertThat(CalendarLabelActions.getLabels(getServerPort(), accessTokenId))
            .returns(labelId, LabelResponse::getLabelId)
            .returns(LABEL, LabelResponse::getLabel);
    }

    private void create_alreadyExists(UUID accessTokenId) {
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getCreateLabelResponse(getServerPort(), accessTokenId, LABEL), "label", "already exists");
    }

    private UUID create(UUID accessTokenId) {
        UUID labelId = CalendarLabelActions.createLabel(getServerPort(), accessTokenId, LABEL);
        assertThat(labelId).isNotNull();

        return labelId;
    }

    private void create_tooLong(UUID accessTokenId) {
        String label = "a".repeat(256);
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getCreateLabelResponse(getServerPort(), accessTokenId, label), "label", "too long");
    }

    private void create_blank(UUID accessTokenId) {
        ResponseValidator.verifyInvalidParam(CalendarLabelActions.getCreateLabelResponse(getServerPort(), accessTokenId, " "), "label", "must not be null or blank");
    }
}
