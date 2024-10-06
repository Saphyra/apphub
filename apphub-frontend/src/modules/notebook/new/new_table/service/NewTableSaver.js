import Constants from "../../../../../common/js/Constants";
import { getBrowserLanguage } from "../../../../../common/js/Utils";
import Stream from "../../../../../common/js/collection/Stream";
import getDefaultErrorHandler from "../../../../../common/js/dao/DefaultErrorHandler";
import { NOTEBOOK_CREATE_TABLE } from "../../../../../common/js/dao/endpoints/NotebookEndpoints";
import { STORAGE_UPLOAD_FILE } from "../../../../../common/js/dao/endpoints/StorageEndpoints";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import OpenedPageType from "../../../common/OpenedPageType";
import validateColumnData from "../../../common/validator/ColumnDataValidator";
import validateListItemTitle from "../../../common/validator/ListItemTitleValidator";
import validateTableHeadNames from "../../../common/validator/TableHeadNameValidator";

const create = async (listItemTitle, tableHeads, parent, checklist, rows, custom, setDisplaySpinner, files) => {
    const titleValidationResult = validateListItemTitle(listItemTitle);
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

    const payload = {
        title: listItemTitle,
        parent: parent,
        listItemType: getListItemType(checklist, custom),
        tableHeads: tableHeads,
        rows: rows
    }

    const fileUploadResponse = await NOTEBOOK_CREATE_TABLE.createRequest(payload)
        .send();

    if (fileUploadResponse.length > 0) {
        uploadFiles(setDisplaySpinner, fileUploadResponse, files);
    }

    window.location.href = Constants.NOTEBOOK_PAGE;
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
            "BrowserLanguage": getBrowserLanguage(),
            "Request-Type": Constants.HEADER_REQUEST_TYPE_VALUE
        }
    }

    await fetch(STORAGE_UPLOAD_FILE.assembleUrl({ storedFileId: fileUpload.storedFileId }), options)
        .then(r => {
            if (!r.ok) {
                setDisplaySpinner(false);
                r.text()
                    .then(body => {
                        const response = new Response(r.status, body);
                        getDefaultErrorHandler()
                            .handle(response);
                    });
            }
        });
}

const getListItemType = (checklist, custom) => {
    if (custom) {
        return OpenedPageType.CUSTOM_TABLE;
    }

    return checklist ? OpenedPageType.CHECKLIST_TABLE : OpenedPageType.TABLE
}

export default create;