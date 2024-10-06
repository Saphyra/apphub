import Constants from "../../../../../common/js/Constants";
import { NOTEBOOK_CREATE_CHECKLIST } from "../../../../../common/js/dao/endpoints/NotebookEndpoints";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import validateListItemTitle from "../../../common/validator/ListItemTitleValidator";

const create = async (listItemTitle, parent, items) => {
    const result = validateListItemTitle(listItemTitle);
    if (!result.valid) {
        NotificationService.showError(result.message);
        return;
    }

    const payload = {
        parent: parent,
        title: listItemTitle,
        items: items
    }

    await NOTEBOOK_CREATE_CHECKLIST.createRequest(payload)
        .send();

    window.location.href = Constants.NOTEBOOK_PAGE;
}

export default create;