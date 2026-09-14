import { LOGOUT } from "modules/etc/index/IndexEndpoints";
import NotificationKey from "./notification/NotificationKey";
import { INDEX_PAGE } from "./GenericEndpoints";

const logout = async (setDisplaySpinner) => {
    await LOGOUT.createRequest()
        .send(setDisplaySpinner);

    sessionStorage.successCode = NotificationKey.SUCCEESSFUL_LOGOUT;
    window.location.href = INDEX_PAGE;
}

export default logout;