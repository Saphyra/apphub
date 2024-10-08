import Constants from "../../../../common/js/Constants";
import { NOTEBOOK_CREATE_LINK } from "../../../../common/js/dao/endpoints/NotebookEndpoints";
import NotificationService from "../../../../common/js/notification/NotificationService";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";
import validateUrl from "../../common/validator/UrlValidator";

const create = async (listItemTitle, url, parent) => {
    const listItemTitleResult = validateListItemTitle(listItemTitle);
    if (!listItemTitleResult.valid) {
        NotificationService.showError(listItemTitleResult.message);
        return;
    }

    const urlResult = validateUrl(url);
    if (!urlResult.valid) {
        NotificationService.showError(urlResult.message);
        return;
    }

    const payload = {
        parent: parent,
        title: listItemTitle,
        url: url
    }
    await NOTEBOOK_CREATE_LINK.createRequest(payload)
        .send();

    window.location.href = Constants.NOTEBOOK_PAGE;
}

export default create;