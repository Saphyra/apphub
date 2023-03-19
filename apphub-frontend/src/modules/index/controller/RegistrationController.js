import Endpoints from "../../../common/js/dao/dao";
import login from "./LoginController";

const register = (username, email, password) => {
    const body = {
        username: username,
        email: email,
        password: password
    }

    Endpoints.ACCOUNT_REGISTER.createRequest(body)
        .send()
        .then(() => login(email, password, false));
}

export default register;