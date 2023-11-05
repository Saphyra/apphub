import Constants from "../../../../../common/js/Constants";
import Endpoints from "../../../../../common/js/dao/dao";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import ListItemType from "../../../common/ListItemType";
import validateListItemTitle from "../../../common/validator/ListItemTitleValidator";
import validateTableHeadNames from "../../../common/validator/TableHeadNameValidator";

const create = async (listItemTitle, tableHeads, parent, checklist, rows) => {
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
        listItemType: checklist ? ListItemType.CHECKLIST_TABLE : ListItemType.TABLE,
        tableHeads: tableHeads,
        rows: rows
    }

    await Endpoints.NOTEBOOK_CREATE_TABLE.createRequest(payload)
        .send();

    window.location.href = Constants.NOTEBOOK_PAGE;
}

export default create;