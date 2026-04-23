import NotificationKey from "common/js/notification/NotificationKey";
import login from "./LoginController";
import { ACCOUNT_REGISTER } from "../IndexEndpoints";

const register = async (username, email, password) => {
    const body = {
        username: username,
        email: email,
        password: password
    }

    await ACCOUNT_REGISTER.createRequest(body)
        .send();

    sessionStorage.successCode = NotificationKey.REGISTRATION_SUCCESSFUL;

    login(email, password, false)
}

export default register;