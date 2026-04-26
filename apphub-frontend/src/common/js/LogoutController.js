import { LOGOUT } from "modules/etc/index/IndexEndpoints";
import NotificationKey from "./notification/NotificationKey";
import { INDEX_PAGE } from "./GenericEndpoints";

const logout = async () => {
    await LOGOUT.createRequest()
        .send();

    sessionStorage.successCode = NotificationKey.SUCCEESSFUL_LOGOUT;
    window.location.href = INDEX_PAGE;
}

export default logout;