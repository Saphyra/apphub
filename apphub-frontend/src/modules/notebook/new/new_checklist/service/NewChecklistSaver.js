import Constants from "../../../../../common/js/Constants";
import Endpoints from "../../../../../common/js/dao/dao";
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

    await Endpoints.NOTEBOOK_CREATE_CHECKLIST.createRequest(payload)
        .send();

    window.location.href = Constants.NOTEBOOK_PAGE;
}

export default create;