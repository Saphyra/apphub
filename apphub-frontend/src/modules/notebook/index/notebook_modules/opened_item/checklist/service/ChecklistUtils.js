import ConfirmationDialogData from "../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../common/component/input/Button";
import ListItemType from "../../../../../common/ListItemType";
import { loadChecklist } from "./ChecklistDao";

export const discard = (setConfirmationDialogData, localizationHandler, setEditingEnabled, listItemId, setDataFromResponse) => {
    setConfirmationDialogData(new ConfirmationDialogData(
        "notebook-content-checklist-discard-confirmation",
        localizationHandler.get("confirm-discard-title"),
        localizationHandler.get("confirm-discard-content"),
        [
            <Button
                key="discard"
                id="notebook-content-checklist-discard-confirm-button"
                label={localizationHandler.get("discard")}
                onclick={() => {
                    setEditingEnabled(false);
                    loadChecklist(listItemId, setDataFromResponse);
                    setConfirmationDialogData(null);
                }}
            />,
            <Button
                key="cancel"
                id="notebook-content-checklist-discard-cancel-button"
                label={localizationHandler.get("cancel")}
                onclick={() => setConfirmationDialogData(null)}
            />
        ]
    ));
}

export const close = (editingEnabled, setConfirmationDialogData, localizationHandler, setOpenedListItem, parent) => {
    if (editingEnabled) {
        setConfirmationDialogData(new ConfirmationDialogData(
            "notebook-content-checklist-close-confirmation",
            localizationHandler.get("confirm-close-title"),
            localizationHandler.get("confirm-close-content"),
            [
                <Button
                    key="close"
                    id="notebook-content-checklist-close-confirm-button"
                    label={localizationHandler.get("close")}
                    onclick={() => doClose(setOpenedListItem, parent, setConfirmationDialogData)}
                />,
                <Button
                    key="cancel"
                    id="notebook-content-checklist-close-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    } else {
        doClose(setOpenedListItem, parent, setConfirmationDialogData);
    }
}

const doClose = (setOpenedListItem, parent, setConfirmationDialogData) => {
    setOpenedListItem({ id: parent, type: ListItemType.CATEGORY })
    setConfirmationDialogData(null);
}