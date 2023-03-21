import Constants from "../../../common/js/Constants";
import Endpoints from "../../../common/js/dao/dao";
import NotificationKey from "../../../common/js/notification/NotificationKey";
import NotificationService from "../../../common/js/notification/NotificationService";

const logout = async () => {
    await Endpoints.LOGOUT.createRequest()
        .send();

    sessionStorage.successCode = NotificationKey.SUCCEESSFUL_LOGOUT;
    window.location.href = Constants.INDEX_PAGE;
}

export default logout;