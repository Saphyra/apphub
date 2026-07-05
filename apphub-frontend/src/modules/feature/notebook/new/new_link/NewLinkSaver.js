import NotificationService from "common/js/notification/NotificationService";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";
import validateUrl from "../../common/validator/UrlValidator";
import { NOTEBOOK_CREATE_LINK, NOTEBOOK_PAGE } from "../../NotebookEndpoints";

const create = async (listItemTitle, url, parent, setDisplaySpinner) => {
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
        .send(setDisplaySpinner);

    window.location.href = NOTEBOOK_PAGE;
}

export default create;