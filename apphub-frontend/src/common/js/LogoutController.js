import Constants from "./Constants";
import Endpoints from "./dao/dao";
import NotificationKey from "./notification/NotificationKey";

const logout = async () => {
    await Endpoints.LOGOUT.createRequest()
        .send();

    sessionStorage.successCode = NotificationKey.SUCCEESSFUL_LOGOUT;
    window.location.href = Constants.INDEX_PAGE;
}

export default logout;