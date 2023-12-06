import Constants from "../../../../common/js/Constants";
import Endpoints from "../../../../common/js/dao/dao";
import NotificationService from "../../../../common/js/notification/NotificationService";
import ListItemType from "../../common/ListItemType";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";
import validateUrl from "../../common/validator/UrlValidator";

const save = async (listItemTitle, listItemType, value, parent, listItemId) => {
    const listItemTitleResult = validateListItemTitle(listItemTitle);
    if (!listItemTitleResult.valid) {
        NotificationService.showError(listItemTitleResult.message);
        return;
    }

    if (listItemType == ListItemType.LINK) {
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

    await Endpoints.NOTEBOOK_EDIT_LIST_ITEM.createRequest(payload, { listItemId: listItemId })
        .send();

    window.location.href = Constants.NOTEBOOK_PAGE;
}

export default save;