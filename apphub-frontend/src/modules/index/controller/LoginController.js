import Constants from "../../../common/js/Constants";
import Endpoints from "../../../common/js/dao/dao";
import NotificationKey from "../../../common/js/notification/NotificationKey";
import NotificationService from "../../../common/js/notification/NotificationService";
import Utils from "../../../common/js/Utils";

const login = async (email, password, rememberMe) => {
    if (email.length === 0 || password.length === 0) {
        NotificationService.showErrorCode(NotificationKey.EMPTY_CREDENTIALS);
        return;
    }

    const body = {
        email: email,
        password: password,
        rememberMe: rememberMe
    }

    const loginResponse = await Endpoints.LOGIN.createRequest(body)
        .send();

    Utils.setCookie("access-token", loginResponse.accessTokenId, loginResponse.expirationDays);
    if (sessionStorage.userEmail !== email) {
        sessionStorage.clear();
    }
    sessionStorage.userEmail = email;
    window.location.href = Utils.getQueryParam("redirect") || Constants.MODULES_PAGE;
}

export default login;