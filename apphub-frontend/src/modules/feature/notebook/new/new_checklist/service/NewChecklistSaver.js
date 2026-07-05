import Constants from "common/js/Constants";
import NotificationService from "common/js/notification/NotificationService";
import validateListItemTitle from "modules/feature/notebook/common/validator/ListItemTitleValidator";
import { NOTEBOOK_CREATE_CHECKLIST, NOTEBOOK_PAGE } from "modules/feature/notebook/NotebookEndpoints";

const create = async (listItemTitle, parent, items, setDisplaySpinner) => {
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
        .send(setDisplaySpinner);

    window.location.href = NOTEBOOK_PAGE;
}

export default create;