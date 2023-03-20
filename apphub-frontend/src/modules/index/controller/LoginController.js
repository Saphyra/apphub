import Constants from "../../../common/js/Constants";
import Endpoints from "../../../common/js/dao/dao";
import Utils from "../../../common/js/Utils";

const login = async (email, password, rememberMe) => {
    const body = {
        email: email,
        password: password,
        rememberMe: rememberMe
    }

    const loginResponse = await Endpoints.LOGIN.createRequest(body)
        .send();

    Utils.setCookie("access-token", loginResponse.accessTokenId, loginResponse.expirationDays);
    window.location.href = Utils.getQueryParam("redirect") || Constants.MODULES_PAGE;
}

export default login;