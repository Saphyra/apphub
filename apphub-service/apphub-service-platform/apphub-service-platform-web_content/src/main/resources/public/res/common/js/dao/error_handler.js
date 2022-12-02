(function ErrorHandler(){
    const errorResponseParameterTranslators = [];

    window.errorHandler = new function(){
        this.addErrorResponseParameterTranslator = function(errorResponseParameterTranslator){
            if(!errorResponseParameterTranslator instanceof ErrorResponseParameterTranslator){
                throwException("IllegalArgument", "input is not an ErrorResponseParameterTranslator.");
            }

            console.log("Adding ErrorResponseParameterTranslator", errorResponseParameterTranslator);

            errorResponseParameterTranslators.push(errorResponseParameterTranslator);
        }
        this.assembleLocalization = assembleLocalization;
    }

    function assembleLocalization(errorResponse){
        let result = errorResponse.localizedMessage;

        new MapStream(errorResponse.params)
            .map((key, value) => {
                return new Stream(errorResponseParameterTranslators)
                    .findFirst((translator) => {return translator.canTranslate(key)})
                    .map((translator) => {return translator.translate(value)})
                    .orElse(value);
            })
            .forEach((key, value) => result = result.replace("{" + key + "}", value));

        return result;
    }
})();

function ErrorHandlerRegistry(){
    const handlers = [];

    const defaultErrorHandler = new ErrorHandler(
        function(){return true},
        function(request, response){
            console.log("Using default error handler...");
            if(isErrorResponse(response.body)){
                console.log("Handling errorResponse: " + response.toString());
                const errorResponse = JSON.parse(response.body);

                switch(errorResponse.errorCode){
                    case "SESSION_EXPIRED":
                        sessionStorage.errorMessage = "session-expired";
                        eventProcessor.processEvent(new Event(events.LOGOUT));
                    break;
                    default:
                        notificationService.showError(errorHandler.assembleLocalization(errorResponse));
                }
            }else{
                notificationService.showError("Error response from BackEnd: " + response.toString());
            }

            if(typeof spinner !== "undefined"){
                spinner.close();
            }

            function isErrorResponse(responseBody){
                try{
                    if(responseBody.length == 0){
                        console.log("Empty response body");
                        return false;
                    }

                    const errorResponse = JSON.parse(responseBody);
                    console.log("ErrorResponse", errorResponse);

                    return errorResponse.errorCode !== undefined
                        && errorResponse.localizedMessage !== undefined
                        && errorResponse.params !== undefined;
                }catch(e){
                    console.log(e);
                    return false;
                }
            }
        }
    );

    this.addErrorHandler = function(errorHandler){
        if(!errorHandler){
            throwException("IllegalArgument", "errorHandler is null.");
        }

        if(!errorHandler instanceof ErrorHandler){
            throwException("IllegalArgument", "errorHandler is not a type of ErrorHandler");
        }

        handlers.push(errorHandler);

        return this;
    }

    this.handleError = function(request, response){
        console.log("Processing error...");
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
                console.log("ErrorHandler found", handler);
                setTimeout(function(){handler.handle(request, response), 0});
                foundProcessor = true;
            }
        }

        if(!foundProcessor){
            console.log("No errorHandler found, using default one...");
            defaultErrorHandler.handle(request, response);
        }
    }
};

function ErrorHandler(canHandle, handle){
    this.canHandle = canHandle;
    this.handle = handle;
}

function ErrorResponseParameterTranslator(canTranslate, translate){
    this.canTranslate = canTranslate;
    this.translate = translate;
}