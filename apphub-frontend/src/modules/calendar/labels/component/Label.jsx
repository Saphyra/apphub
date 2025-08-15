import { useState } from "react";
import Button from "../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import { CALENDAR_DELETE_LABEL, CALENDAR_EDIT_LABEL } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import PreLabeledInputField from "../../../../common/component/input/PreLabeledInputField";
import InputField from "../../../../common/component/input/InputField";
import {  isBlank } from "../../../../common/js/Utils";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import NotificationService from "../../../../common/js/notification/NotificationService";
import { MAX_LABEL_LENGTH } from "../../CalendarConstants";
import Stream from "../../../../common/js/collection/Stream";

const Label = ({
    labelId,
    label,
    setDisplaySpinner,
    localizationHandler,
    setConfirmationDialogData,
    labels,
    setLabels,
    selected,
    setSelectedLabel
}) => {
    const [newLabel, setNewLabel] = useState(label);
    const [displayEditDialog, setDisplayEditDialog] = useState(false);

    return (
        <div
            className={"calendar-labels-label button" + (selected ? " selected" : "")}
            onClick={() => setSelectedLabel((labelId))}
        >
            <span className="calendar-labels-label-title">{label}</span>

            <div className="calendar-labels-label-operations">
                <Button
                    className="calendar-labels-label-delete"
                    label={"X"}
                    onclick={() => confirmLabelDeletion()}
                />
                <Button
                    className="calendar-labels-label-edit"
                    label={"X"}
                    onclick={() => setDisplayEditDialog(true)}
                />
            </div>

            {displayEditDialog &&
                <ConfirmationDialog
                    id={"calendar-labels-edit-label"}
                    title={localizationHandler.get("rename-label")}
                    content={
                        <PreLabeledInputField
                            label={localizationHandler.get("new-label")}
                            input={<InputField
                                id="calendar-labels-new-name"
                                placeholder={localizationHandler.get("new-label")}
                                value={newLabel}
                                onchangeCallback={setNewLabel}
                            />}
                        />
                    }
                    choices={[
                        <Button
                            key="save"
                            id="calendar-labels-edit-label-save"
                            label={localizationHandler.get("save")}
                            onclick={() => renameLabel()}
                        />,
                        <Button
                            key="cancel"
                            id="calendar-labels-delete-label-cancel"
                            label={localizationHandler.get("cancel")}
                            onclick={() => setDisplayEditDialog(false)}
                        />
                    ]}
                />
            }
        </div>
    );

    async function renameLabel() {
        if (isBlank(newLabel)) {
            NotificationService.showError(localizationHandler.get("label-too-short"));
            return
        }

        if (newLabel.length > MAX_LABEL_LENGTH) {
            NotificationService.showError(localizationHandler.get("nlabel-too-long"));
            return;
        }

        if (new Stream(labels).anyMatch(l => l.label === newLabel)) {
            NotificationService.showError(localizationHandler.get("label-already-exists"));
            return;
        }

        const response = await CALENDAR_EDIT_LABEL.createRequest({ value: newLabel }, { labelId: labelId })
            .send(setDisplaySpinner);

        setLabels(response);
        setDisplayEditDialog(false);
    }

    function confirmLabelDeletion() {
        setConfirmationDialogData(new ConfirmationDialogData(
            "calendar-labels-delete-confirmation",
            localizationHandler.get("delete-label-confirmation-dialog-title"),
            localizationHandler.get("delete-label-confirmation-dialog-content", { label: label }),
            [
                <Button
                    key="confirm"
                    id="calendar-labels-delete-label-confirm"
                    label={localizationHandler.get("delete")}
                    onclick={() => deleteLabel()}
                />,
                <Button
                    key="cancel"
                    id="calendar-labels-delete-label-cancel"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ))
    }

    async function deleteLabel() {
        const response = await CALENDAR_DELETE_LABEL.createRequest(null, { labelId: labelId })
            .send(setDisplaySpinner);

        setLabels(response);
        setConfirmationDialogData(null);
        if (selected) {
            setSelectedLabel(null);
        }
    }
}

export default Label;