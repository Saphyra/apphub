import { CHECK_SESSION } from "./GenericEndpoints";
import getDefaultErrorHandler from "./dao/DefaultErrorHandler";
import ErrorHandler from "./dao/ErrorHandler";
import ResponseStatus from "./dao/ResponseStatus";

const sessionChecker = () => {
    setInterval(checkSession, 10000);
}

const checkSession = () => {
    CHECK_SESSION.createRequest()
        .addErrorHandler(new ErrorHandler(
            response => response.status === ResponseStatus.UNAUTHORIZED,
            response => getDefaultErrorHandler().handle(response)
        ))
        .addErrorHandler(new ErrorHandler(
            response => response.status !== ResponseStatus.UNAUTHORIZED,
            () => { }
        ))
        .send();
}

export default sessionChecker;