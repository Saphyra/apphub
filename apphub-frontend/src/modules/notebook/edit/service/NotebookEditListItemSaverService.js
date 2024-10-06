import Constants from "../../../../common/js/Constants";
import { NOTEBOOK_EDIT_LIST_ITEM } from "../../../../common/js/dao/endpoints/NotebookEndpoints";
import NotificationService from "../../../../common/js/notification/NotificationService";
import OpenedPageType from "../../common/OpenedPageType";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";
import validateUrl from "../../common/validator/UrlValidator";

const save = async (listItemTitle, listItemType, value, parent, listItemId) => {
    const listItemTitleResult = validateListItemTitle(listItemTitle);
    if (!listItemTitleResult.valid) {
        NotificationService.showError(listItemTitleResult.message);
        return;
    }

    if (listItemType == OpenedPageType.LINK) {
        const urlResult = validateUrl(value);
        if (!urlResult.valid) {
            NotificationService.showError(urlResult.message);
            return;
        }
    }

    const payload = {
        parent: parent,
        title: listItemTitle,
        value: value
    }

    await NOTEBOOK_EDIT_LIST_ITEM.createRequest(payload, { listItemId: listItemId })
        .send();

    window.location.href = Constants.NOTEBOOK_PAGE;
}

export default save;