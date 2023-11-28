import Constants from "../../../../../common/js/Constants";
import Endpoints from "../../../../../common/js/dao/dao";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import ListItemType from "../../../common/ListItemType";
import validateListItemTitle from "../../../common/validator/ListItemTitleValidator";
import validateTableHeadNames from "../../../common/validator/TableHeadNameValidator";

const create = async (listItemTitle, tableHeads, parent, checklist, rows, custom) => {
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

    const payload = {
        title: listItemTitle,
        parent: parent,
        listItemType: getListItemType(checklist, custom),
        tableHeads: tableHeads,
        rows: rows
    }

    const fileUploadResponse = await Endpoints.NOTEBOOK_CREATE_TABLE.createRequest(payload)
        .send();

    window.location.href = Constants.NOTEBOOK_PAGE;
}

const getListItemType = (checklist, custom) => {
    if (custom) {
        return ListItemType.CUSTOM_TABLE;
    }

    return checklist ? ListItemType.CHECKLIST_TABLE : ListItemType.TABLE
}

export default create;