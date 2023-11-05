import ConfirmationDialogData from "../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../common/component/input/Button";
import Utils from "../../../../../../../common/js/Utils";
import Endpoints from "../../../../../../../common/js/dao/dao";
import EventName from "../../../../../../../common/js/event/EventName";
import NotificationService from "../../../../../../../common/js/notification/NotificationService";
import validateListItemTitle from "../../../../../common/validator/ListItemTitleValidator";
import validateTableHeadNames from "../../../../../common/validator/TableHeadNameValidator";

export const loadTable = (listItemId, setDataFromResponse) => {
    const fetch = async () => {
        const response = await Endpoints.NOTEBOOK_GET_TABLE.createRequest(null, { listItemId: listItemId })
            .send();

        setDataFromResponse(response);
    }
    fetch();
}

export const save = async (title, tableHeads, rows, listItemId, setEditingEnabled, setLastEvent, setDataFromResponse) => {
    const titleValidationResult = validateListItemTitle(title);
    if (!titleValidationResult.valid) {
        NotificationService.showError(titleValidationResult.message);
        return;
    }

    const tableHeadNameValidationResult = validateTableHeadNames(tableHeads);
    if (!tableHeadNameValidationResult.valid) {
        NotificationService.showError(tableHeadNameValidationResult.message);
        return;
    }

    const tablePayload = {
        title: title,
        tableHeads: tableHeads,
        rows: rows
    }

    const response = await Endpoints.NOTEBOOK_EDIT_TABLE.createRequest(tablePayload, { listItemId: listItemId })
        .send();

    setEditingEnabled(false);
    setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_MODIFIED));
    setDataFromResponse(response.tableResponse);
}

export const confirmDeleteChcecked = (setConfirmationDialogData, localizationHandler, listItemId, setDataFromResponse) => {
    setConfirmationDialogData(new ConfirmationDialogData(
        "notebook-content-table-delete-checked-confirmation",
        localizationHandler.get("confirm-delete-checked-title"),
        localizationHandler.get("confirm-delete-checked-content"),
        [
            <Button
                key="delete"
                id="notebook-content-table-delete-checked-confirm-button"
                label={localizationHandler.get("delete-checked")}
                onclick={() => deleteChecked(listItemId, setDataFromResponse, setConfirmationDialogData)}
            />,
            <Button
                key="cancel"
                id="notebook-content-table-delete-checked-cancel-button"
                label={localizationHandler.get("cancel")}
                onclick={() => setConfirmationDialogData(null)}
            />
        ]
    ));
}

const deleteChecked = async (listItemId, setDataFromResponse, setConfirmationDialogData) => {
    const response = await Endpoints.NOTEBOOK_TABLE_DELETE_CHECKED.createRequest(null, { listItemId: listItemId })
        .send();

    setDataFromResponse(response);
    setConfirmationDialogData(null);
}

export const updateChecked = (row, rows, setRows, editingEnabled) => {
    if (!editingEnabled) {
        Endpoints.NOTEBOOK_TABLE_SET_ROW_STATUS.createRequest({ value: row.checked }, { rowId: row.rowId })
            .send();
    }

    Utils.copyAndSet(rows, setRows);
}