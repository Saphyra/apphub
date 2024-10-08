import Constants from "../../../../common/js/Constants";
import { NOTEBOOK_CREATE_TEXT } from "../../../../common/js/dao/endpoints/NotebookEndpoints";
import NotificationService from "../../../../common/js/notification/NotificationService";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";

const create = async (listItemTitle, parent, content) => {
    const result = validateListItemTitle(listItemTitle);
    if (!result.valid) {
        NotificationService.showError(result.message);
        return;
    }

    const payload = {
        parent: parent,
        title: listItemTitle,
        content: content
    }

    await NOTEBOOK_CREATE_TEXT.createRequest(payload)
        .send();

    window.location.href = Constants.NOTEBOOK_PAGE;
}

export default create;