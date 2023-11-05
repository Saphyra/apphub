import ConfirmationDialogData from "../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../common/component/input/Button";
import Endpoints from "../../../../../../../common/js/dao/dao";
import EventName from "../../../../../../../common/js/event/EventName";
import NotificationService from "../../../../../../../common/js/notification/NotificationService";
import validateListItemTitle from "../../../../../common/validator/ListItemTitleValidator";

export const loadChecklist = (listItemId, setDataFromResponse) => {
    const fetch = async () => {
        const response = await Endpoints.NOTEBOOK_GET_CHECKLIST.createRequest(null, { listItemId: listItemId })
            .send();

        setDataFromResponse(response);
    }
    fetch();
}

export const save = async (title, items, openedListItem, setEditingEnabled, setLastEvent, setDataFromResponse) => {
    const result = validateListItemTitle(title);
    if (!result.valid) {
        NotificationService.showError(result.message);
        return;
    }

    const payload = {
        title: title,
        items: items
    }

    const response = await Endpoints.NOTEBOOK_EDIT_CHECKLIST.createRequest(payload, { listItemId: openedListItem.id })
        .send();

    setEditingEnabled(false);
    setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_MODIFIED));
    setDataFromResponse(response);
}

export const confirmDeleteChcecked = (setConfirmationDialogData, localizationHandler, openedListItem, setDataFromResponse) => {
    setConfirmationDialogData(new ConfirmationDialogData(
        "notebook-content-checklist-delete-checked-confirmation",
        localizationHandler.get("confirm-delete-checked-title"),
        localizationHandler.get("confirm-delete-checked-content"),
        [
            <Button
                key="delete"
                id="notebook-content-checklist-delete-checked-confirm-button"
                label={localizationHandler.get("delete-checked")}
                onclick={() => deleteChecked(setDataFromResponse, setConfirmationDialogData)}
            />,
            <Button
                key="cancel"
                id="notebook-content-checklist-delete-checked-cancel-button"
                label={localizationHandler.get("cancel")}
                onclick={() => setConfirmationDialogData(null)}
            />
        ]
    ));

    const deleteChecked = async (setDataFromResponse, setConfirmationDialogData) => {
        const response = await Endpoints.NOTEBOOK_CHECKLIST_DELETE_CHECKED.createRequest(null, { listItemId: openedListItem.id })
            .send();

        setDataFromResponse(response);
        setConfirmationDialogData(null);
    }
}

export const orderItems = async (listItemId, setDataFromResponse) => {
    const response = await Endpoints.NOTEBOOK_ORDER_CHECKLIST_ITEMS.createRequest(null, { listItemId: listItemId })
        .send();

    setDataFromResponse(response);
}