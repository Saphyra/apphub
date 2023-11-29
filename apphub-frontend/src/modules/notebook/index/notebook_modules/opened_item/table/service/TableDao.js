import ConfirmationDialogData from "../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../common/component/input/Button";
import Constants from "../../../../../../../common/js/Constants";
import Utils from "../../../../../../../common/js/Utils";
import Stream from "../../../../../../../common/js/collection/Stream";
import getDefaultErrorHandler from "../../../../../../../common/js/dao/DefaultErrorHandler";
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

    const tablePayload = {
        title: title,
        tableHeads: tableHeads,
        rows: rows
    }

    const response = await Endpoints.NOTEBOOK_EDIT_TABLE.createRequest(tablePayload, { listItemId: listItemId })
        .send();

    uploadFiles(setDisplaySpinner, response.fileUpload, files);

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
    console.log(file);
    formData.append("file", file.e.target.files[0]);

    const options = {
        method: "PUT",
        body: formData,
        headers: {
            'Cache-Control': "no-cache",
            "BrowserLanguage": Utils.getBrowserLanguage(),
            "Request-Type": Constants.HEADER_REQUEST_TYPE_VALUE
        }
    }

    await fetch(Endpoints.STORAGE_UPLOAD_FILE.assembleUrl({ storedFileId: fileUpload.storedFileId }), options)
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