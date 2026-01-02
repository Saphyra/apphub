import ConfirmationDialogData from "../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../common/component/input/Button";
import { copyAndSet, getBrowserLanguage } from "../../../../../../../common/js/Utils";
import Stream from "../../../../../../../common/js/collection/Stream";
import getDefaultErrorHandler from "../../../../../../../common/js/dao/DefaultErrorHandler";
import { NOTEBOOK_EDIT_TABLE, NOTEBOOK_GET_TABLE, NOTEBOOK_TABLE_DELETE_CHECKED, NOTEBOOK_TABLE_SET_ROW_STATUS } from "../../../../../../../common/js/dao/endpoints/NotebookEndpoints";
import { STORAGE_UPLOAD_FILE } from "../../../../../../../common/js/dao/endpoints/StorageEndpoints";
import EventName from "../../../../../../../common/js/event/EventName";
import NotificationService from "../../../../../../../common/js/notification/NotificationService";
import validateColumnData from "../../../../../common/validator/ColumnDataValidator";
import validateListItemTitle from "../../../../../common/validator/ListItemTitleValidator";
import validateTableHeadNames from "../../../../../common/validator/TableHeadNameValidator";

export const loadTable = (listItemId, setDataFromResponse, setDisplaySpinner) => {
    const fetch = async () => {
        const response = await NOTEBOOK_GET_TABLE.createRequest(null, { listItemId: listItemId })
            .send(setDisplaySpinner);

        setDataFromResponse(response);
    }
    fetch();
}

export const save = async (
    title,
    tableHeads,
    rows,
    listItemId,
    setEditingEnabled,
    setLastEvent,
    setDataFromResponse,
    files,
    setDisplaySpinner
) => {
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

    const columnValidationResult =  validateColumnData(rows);
    if (!columnValidationResult.valid) {
        NotificationService.showError(columnValidationResult.message);
        return;
    }

    const tablePayload = {
        title: title,
        tableHeads: tableHeads,
        rows: rows
    }

    const response = await NOTEBOOK_EDIT_TABLE.createRequest(tablePayload, { listItemId: listItemId })
        .send(setDisplaySpinner);

    if (response.fileUpload.length > 0) {
        uploadFiles(setDisplaySpinner, response.fileUpload, files);
    }

    setEditingEnabled(false);
    setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_MODIFIED));
    setDataFromResponse(response.tableResponse);
}

const uploadFiles = async (setDisplaySpinner, fileUploadResponse, files) => {
    setDisplaySpinner(true);

    new Stream(fileUploadResponse)
        .forEach(fileUpload => uploadFile(fileUpload, files, setDisplaySpinner));
}

const uploadFile = async (fileUpload, files, setDisplaySpinner) => {
    new Stream(files)
        .filter(file => file.rowIndex == fileUpload.rowIndex && file.columnIndex == fileUpload.columnIndex)
        .map(file => file.file)
        .findFirst()
        .ifPresent((file) => doUpload(fileUpload, file, setDisplaySpinner));
}

const doUpload = async (fileUpload, file, setDisplaySpinner) => {
    const formData = new FormData();
    formData.append("file", file.e.target.files[0]);

    const options = {
        method: "PUT",
        body: formData,
        headers: {
            'Cache-Control': "no-cache",
            "BrowserLanguage": getBrowserLanguage()
        }
    }

    await fetch(STORAGE_UPLOAD_FILE.assembleUrl({ storedFileId: fileUpload.storedFileId }), options)
        .then(r => {
            setDisplaySpinner(false);

            if (!r.ok) {
                r.text()
                    .then(body => {
                        const response = new Response(r.status, body);
                        getDefaultErrorHandler()
                            .handle(response);
                    });
            }
        });
}

export const confirmDeleteChcecked = (setConfirmationDialogData, localizationHandler, listItemId, setDataFromResponse, setDisplaySpinner) => {
    setConfirmationDialogData(new ConfirmationDialogData(
        "notebook-content-table-delete-checked-confirmation",
        localizationHandler.get("confirm-delete-checked-title"),
        localizationHandler.get("confirm-delete-checked-content"),
        [
            <Button
                key="delete"
                id="notebook-content-table-delete-checked-confirm-button"
                label={localizationHandler.get("delete-checked")}
                onclick={() => deleteChecked(listItemId, setDataFromResponse, setConfirmationDialogData, setDisplaySpinner)}
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

const deleteChecked = async (listItemId, setDataFromResponse, setConfirmationDialogData, setDisplaySpinner) => {
    const response = await NOTEBOOK_TABLE_DELETE_CHECKED.createRequest(null, { listItemId: listItemId })
        .send(setDisplaySpinner);

    setDataFromResponse(response);
    setConfirmationDialogData(null);
}

export const updateChecked = (row, rows, setRows, editingEnabled, setDisplaySpinner) => {
    if (!editingEnabled) {
        NOTEBOOK_TABLE_SET_ROW_STATUS.createRequest({ value: row.checked }, { rowId: row.rowId })
            .send(setDisplaySpinner);
    }

    copyAndSet(rows, setRows);
}