import Constants from "../../../common/js/Constants";
import Endpoints from "../../../common/js/dao/dao";
import NotificationKey from "../../../common/js/notification/NotificationKey";
import NotificationService from "../../../common/js/notification/NotificationService";
import Utils from "../../../common/js/Utils";

const login = async (userIdentifier, password, rememberMe) => {
    if (userIdentifier.length === 0 || password.length === 0) {
        NotificationService.showErrorCode(NotificationKey.EMPTY_CREDENTIALS);
        return;
    }

    const body = {
        userIdentifier: userIdentifier,
        password: password,
        rememberMe: rememberMe
    }

    const loginResponse = await Endpoints.LOGIN.createRequest(body)
        .send();

    Utils.setCookie("access-token", loginResponse.accessTokenId, loginResponse.expirationDays);
    //Clear sessionStorage so stored values of a different user does not cause problems
    if (sessionStorage.userIdentifier !== userIdentifier) {
        sessionStorage.clear();
    }
    sessionStorage.userIdentifier = userIdentifier;
    window.location.href = Utils.getQueryParam("redirect") || Constants.MODULES_PAGE;
}

export default login;