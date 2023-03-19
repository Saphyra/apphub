const ErrorHandler = class {
    constructor(canHandle, handle) {
        this.canHandle = canHandle;
        this.handle = handle;
    }
}

export default ErrorHandler;