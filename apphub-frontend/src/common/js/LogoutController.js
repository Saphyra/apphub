import Constants from "./Constants";
import { LOGOUT } from "./dao/endpoints/UserEndpoints";
import NotificationKey from "./notification/NotificationKey";

const logout = async () => {
    await LOGOUT.createRequest()
        .send();

    sessionStorage.successCode = NotificationKey.SUCCEESSFUL_LOGOUT;
    window.location.href = Constants.INDEX_PAGE;
}

export default logout;