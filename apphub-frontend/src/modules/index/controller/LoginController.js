import Constants from "../../../common/js/Constants";
import Endpoints from "../../../common/js/dao/dao";
import Utils from "../../../common/js/Utils";

const login = (email, password, rememberMe) => {
    const body = {
        email: email,
        password: password,
        rememberMe: rememberMe
    }

    Endpoints.LOGIN.createRequest(body)
        .send()
        .then((loginResponse) => {
            Utils.setCookie("access-token", loginResponse.accessTokenId, loginResponse.expirationDays);
            window.location.href = Utils.getQueryParam("redirect") || Constants.MODULES_PAGE;
        })
}

export default login;