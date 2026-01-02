import ConfirmationDialogData from "../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../common/component/input/Button";
import { NOTEBOOK_ADD_CHECKLIST_ITEM, NOTEBOOK_CHECKLIST_DELETE_CHECKED, NOTEBOOK_EDIT_CHECKLIST, NOTEBOOK_GET_CHECKLIST, NOTEBOOK_ORDER_CHECKLIST_ITEMS } from "../../../../../../../common/js/dao/endpoints/NotebookEndpoints";
import EventName from "../../../../../../../common/js/event/EventName";
import NotificationService from "../../../../../../../common/js/notification/NotificationService";
import validateListItemTitle from "../../../../../common/validator/ListItemTitleValidator";

export const loadChecklist = (listItemId, setDataFromResponse, setDisplaySpinner) => {
    const fetch = async () => {
        const response = await NOTEBOOK_GET_CHECKLIST.createRequest(null, { listItemId: listItemId })
            .send(setDisplaySpinner);

        setDataFromResponse(response);
    }
    fetch();
}

export const save = async (title, items, openedListItem, setEditingEnabled, setLastEvent, setDataFromResponse, setDisplaySpinner) => {
    const result = validateListItemTitle(title);
    if (!result.valid) {
        NotificationService.showError(result.message);
        return;
    }

    const payload = {
        title: title,
        items: items
    }

    const response = await NOTEBOOK_EDIT_CHECKLIST.createRequest(payload, { listItemId: openedListItem.id })
        .send(setDisplaySpinner);

    setEditingEnabled(false);
    setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_MODIFIED));
    setDataFromResponse(response);
}

export const confirmDeleteChcecked = (setConfirmationDialogData, localizationHandler, openedListItem, setDataFromResponse, setDisplaySpinner) => {
    setConfirmationDialogData(new ConfirmationDialogData(
        "notebook-content-checklist-delete-checked-confirmation",
        localizationHandler.get("confirm-delete-checked-title"),
        localizationHandler.get("confirm-delete-checked-content"),
        [
            <Button
                key="delete"
                id="notebook-content-checklist-delete-checked-confirm-button"
                label={localizationHandler.get("delete-checked")}
                onclick={() => deleteChecked(setDataFromResponse, setConfirmationDialogData, setDisplaySpinner)}
            />,
            <Button
                key="cancel"
                id="notebook-content-checklist-delete-checked-cancel-button"
                label={localizationHandler.get("cancel")}
                onclick={() => setConfirmationDialogData(null)}
            />
        ]
    ));

    const deleteChecked = async (setDataFromResponse, setConfirmationDialogData, setDisplaySpinner) => {
        const response = await NOTEBOOK_CHECKLIST_DELETE_CHECKED.createRequest(null, { listItemId: openedListItem.id })
            .send(setDisplaySpinner);

        setDataFromResponse(response);
        setConfirmationDialogData(null);
    }
}

export const orderItems = async (listItemId, setDataFromResponse, setDisplaySpinner) => {
    const response = await NOTEBOOK_ORDER_CHECKLIST_ITEMS.createRequest(null, { listItemId: listItemId })
        .send(setDisplaySpinner);

    setDataFromResponse(response);
}

export const addItemToTheEdge = async (listItemId, index, content, setDataFromResponse, setDisplaySpinner) => {
    const body = {
        index: index,
        content: content
    }

    const response = await NOTEBOOK_ADD_CHECKLIST_ITEM.createRequest(body, { listItemId: listItemId })
        .send(setDisplaySpinner);

    setDataFromResponse(response);
}