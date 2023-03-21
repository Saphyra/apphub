import Endpoints from "./dao/dao";

const sessionChecker = () => {
    setInterval(checkSession, 10000);
}

const checkSession = () => {
    Endpoints.CHECK_SESSION.createRequest()
        .send();
}

export default sessionChecker;