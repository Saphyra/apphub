import Endpoints from "../../../common/js/dao/dao";
import login from "./LoginController";

const register = async (username, email, password) => {
    const body = {
        username: username,
        email: email,
        password: password
    }

    await Endpoints.ACCOUNT_REGISTER.createRequest(body)
        .send();

    login(email, password, false)
}

export default register;