import NotificationService from "common/js/notification/NotificationService";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";
import { NOTEBOOK_CREATE_TEXT, NOTEBOOK_PAGE } from "../../NotebookEndpoints";
import validateText from "../../common/validator/TextValidator";

const create = async (listItemTitle, parent, content, setDisplaySpinner) => {
    const titleValidation = validateListItemTitle(listItemTitle);
    if (!titleValidation.valid) {
        NotificationService.showError(titleValidation.message);
        return;
    }
    const contentValidation = validateText(content);
    if (!contentValidation.valid) {
        NotificationService.showError(contentValidation.message);
        return;
    }

    const payload = {
        parent: parent,
        title: listItemTitle,
        content: content
    }

    await NOTEBOOK_CREATE_TEXT.createRequest(payload)
        .send(setDisplaySpinner);

    window.location.href = NOTEBOOK_PAGE;
}

export default create;