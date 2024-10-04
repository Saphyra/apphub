import getDefaultErrorHandler from "./dao/DefaultErrorHandler";
import ErrorHandler from "./dao/ErrorHandler";
import { ResponseStatus } from "./dao/dao";
import { CHECK_SESSION } from "./dao/endpoints/GenericEndpoints";

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

class LogoutErrorHandler extends ErrorHandler {
    constructor() {
        super(
            response => response.status === ResponseStatus.UNAUTHORIZED,
            response => getDefaultErrorHandler().handle(response)
        )
    }
}



export default sessionChecker;