import NotificationKey from "common/js/notification/NotificationKey";
import NotificationService from "common/js/notification/NotificationService";
import { getQueryParam, setCookie } from "common/js/Utils";
import { LOGIN, USER_DATA_GET_ACCOUNT } from "../IndexEndpoints";
import { MODULES_PAGE } from "modules/etc/modules/ModulesEndpoints";
import Constants from "common/js/Constants";

const login = async (userIdentifier, password, rememberMe, setDisplaySpinner) => {
    if (userIdentifier.length === 0 || password.length === 0) {
        NotificationService.showErrorCode(NotificationKey.EMPTY_CREDENTIALS);
        return;
    }

    const body = {
        userIdentifier: userIdentifier,
        password: password,
        rememberMe: rememberMe
    }

    const loginResponse = await LOGIN.createRequest(body)
        .send(setDisplaySpinner, false);

    setCookie("access-token", loginResponse.accessToken.jwt, loginResponse.accessToken.expiration, loginResponse.accessToken.path);
    setCookie("refresh-token", loginResponse.refreshToken.jwt, loginResponse.refreshToken.expiration, loginResponse.refreshToken.path);

    const user = await USER_DATA_GET_ACCOUNT.createRequest()
        .send(setDisplaySpinner);

    //Clear sessionStorage so stored values of a different user does not cause problems
    if (sessionStorage.userId !== user.userId) {
        sessionStorage.clear();
    }
    sessionStorage.userId = user.userId;
    localStorage[Constants.STORAGE_KEY_LOCALE] = user.locale;

    window.location.href = getQueryParam("redirect") || MODULES_PAGE;
}

export default login;