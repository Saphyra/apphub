(function ErrorResponseErrorHandler(){
    errorHandler.registerHandler(new ErrorHandler(canHandle, handle));

    function canHandle(request, response){
        try{
            const body = response.body;
            if(body.length == 0){
                return false;
            }

            const errorResponse = JSON.parse(body);
            console.log(errorResponse);
            return isErrorResponse(errorResponse);
        }catch(e){
            return false;
        }

        function isErrorResponse(errorResponse){
            return errorResponse.httpStatus
                && errorResponse.errorCode
                && errorResponse.localizedMessage
                && errorResponse.params;
        }
    }

    function handle(request, response){
        logService.logToConsole("Handling errorResponse: " + response.toString());
        const errorResponse = JSON.parse(response.body);

        switch(errorResponse.errorCode){
            case "SESSION_EXPIRED":
                sessionStorage.errorMessage = "session-expired";
                eventProcessor.processEvent(new Event(events.LOGOUT));
            break;
            case "INVALID_REQUEST_CONTEXT":
                eventProcessor.processEvent(new Event(events.LOGOUT));
            break;
            default:
                notificationService.showError(errorResponse.localizedMessage);
        }
    }
})();