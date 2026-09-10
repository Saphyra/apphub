import Stream from "common/js/collection/Stream";
import getDefaultErrorHandler from "common/js/dao/DefaultErrorHandler";
import { STORAGE_UPLOAD_FILE } from "common/js/GenericEndpoints";
import NotificationService from "common/js/notification/NotificationService";
import { getBrowserLanguage } from "common/js/Utils";
import OpenedPageType from "modules/feature/notebook/common/OpenedPageType";
import validateColumnData from "modules/feature/notebook/common/validator/ColumnDataValidator";
import validateListItemTitle from "modules/feature/notebook/common/validator/ListItemTitleValidator";
import validateTableHeadNames from "modules/feature/notebook/common/validator/TableHeadNameValidator";
import { NOTEBOOK_CREATE_TABLE, NOTEBOOK_PAGE } from "modules/feature/notebook/NotebookEndpoints";

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
        .send(setDisplaySpinner);

    if (fileUploadResponse.length > 0) {
        await uploadFiles(setDisplaySpinner, fileUploadResponse, files);
    }

    window.location.href = NOTEBOOK_PAGE;
}

const uploadFiles = async (setDisplaySpinner, fileUploadResponse, files) => {
    setDisplaySpinner(true);

    await Promise.all(
        fileUploadResponse.map(fileUpload => uploadFile(fileUpload, files, setDisplaySpinner))
    );
}

const uploadFile = async (fileUpload, files, setDisplaySpinner) => {
    const file = new Stream(files)
        .filter(file => file.rowIndex == fileUpload.rowIndex && file.columnIndex == fileUpload.columnIndex)
        .map(file => file.file)
        .findFirst();

    if (file.isPresent()) {
        await doUpload(fileUpload, file.get(), setDisplaySpinner);
    }
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