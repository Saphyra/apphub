(function ErrorHandlerRegister(){
    scriptLoader.loadScript("/res/common/js/dao/default_error_handler.js");

    const handlers = [];

    window.errorHandler = new function(){
        this.registerHandler = function(errorHandler){
            if(!errorHandler){
                throwException("IllegalArgument", "errorHandler is null.");
            }

            if(!errorHandler instanceof ErrorHandler){
                throwException("IllegalArgument", "errorHandler is not a type of ErrorHandler");
            }

            handlers.push(errorHandler);
            logService.logToConsole("ErrorHandler registered: " + errorHandler.canHandle);
        }

        this.handleError = function(request, response){
            if(!response){
                throwException("IllegalArgument", "response is null.");
            }

            if(!response instanceof Response){
                throwException("IllegalArgument", "response is not a type of Response");
            }

            let foundProcessor = false;
            for(let hIndex in handlers){
                const handler = handlers[hIndex];
                if(handler.canHandle(request, response)){
                    setTimeout(function(){handler.handle(request, response), 0});
                    foundProcessor = true;
                }
            }

            if(!foundProcessor){
                defaultErrorHandler.handle(request, response);
            }
        }
    }
})();

function ErrorHandler(canHandle, handle){
    this.canHandle = canHandle;
    this.handle = handle;
}