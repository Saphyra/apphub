import ConfirmationDialogData from "../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../common/component/input/Button";
import OpenedPageType from "../../../../../common/OpenedPageType";
import { loadTable } from "./TableDao";

export const discard = (setConfirmationDialogData, localizationHandler, setEditingEnabled, listItemId, setDataFromResponse) => {
    setConfirmationDialogData(new ConfirmationDialogData(
        "notebook-content-table-discard-confirmation",
        localizationHandler.get("confirm-discard-title"),
        localizationHandler.get("confirm-discard-content"),
        [
            <Button
                key="discard"
                id="notebook-content-table-discard-confirm-button"
                label={localizationHandler.get("discard")}
                onclick={() => {
                    setEditingEnabled(false);
                    loadTable(listItemId, setDataFromResponse);
                    setConfirmationDialogData(null);
                }}
            />,
            <Button
                key="cancel"
                id="notebook-content-table-discard-cancel-button"
                label={localizationHandler.get("cancel")}
                onclick={() => setConfirmationDialogData(null)}
            />
        ]
    ));
}

export const close = (editingEnabled, setConfirmationDialogData, localizationHandler, setOpenedListItem, parent) => {
    if (editingEnabled) {
        setConfirmationDialogData(new ConfirmationDialogData(
            "notebook-content-table-close-confirmation",
            localizationHandler.get("confirm-close-title"),
            localizationHandler.get("confirm-close-content"),
            [
                <Button
                    key="close"
                    id="notebook-content-table-close-confirm-button"
                    label={localizationHandler.get("close")}
                    onclick={() => doClose(setOpenedListItem, setConfirmationDialogData, parent)}
                />,
                <Button
                    key="cancel"
                    id="notebook-content-table-close-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    } else {
        doClose(setOpenedListItem, setConfirmationDialogData, parent);
    }
}

const doClose = (setOpenedListItem, setConfirmationDialogData, parent) => {
    setOpenedListItem({ id: parent, type: OpenedPageType.CATEGORY })
    setConfirmationDialogData(null);
}