/*
    Request object for processing async BackEnd calls.
    Fields:
        - method:
        - path:
        - body: The request body. Will be converted to JSON if object.
        - state: helper field for result processing
    Methods:
        - processResponse: will be called by dao when xhr request returns. Parameter: response object of the xhr
        - isResponseOk: determinated if the request is Ok. If it is, processValidResponse, if not, processInvalidResponse will be called.
        - convertResponse: converts the response of the xhr response.
        - processValidResponse: will be called when xhr response is valid.
        - processErrorResponse: will be called when xhr request fails.
        - validate: validates if the Request is valid for sending.
*/
function Request(method, path, body){
    this.method = method;
    this.path = path;
    this.body = processBody(body);
    this.state = {};
    
    function processBody(body){
        if(!hasValue(body)){
            return "";
        }
        if(typeof body == "object"){
            return JSON.stringify(body);
        }
        return body;
    }
    
    this.processResponse = function(response){
        if(this.isResponseOk(response)){
            this.processValidResponse(this.convertResponse(response), this.state);
        }else{
            errorHandler.handleError(this, response);
        }
    }
    
    this.isResponseOk = function(response){
        return response.status === ResponseStatus.OK;
    }
    
    this.convertResponse = function(response){
        return response;
    }
    
    this.processValidResponse = function(payload, state){
        console.log("Using no overridden processValidResponse");
    }
    
    this.processErrorResponse = function(response){
        logService.log(response.toString(), "error", "Invalid response from BackEnd: ");
    }
    
    this.validate = function(){
        if(!this.method || typeof this.method !== "string"){
            throwException("IllegalArgument", "method must be a string.");
        }
        this.method = this.method.toUpperCase();
        if(HttpMethod.allowedMethods.indexOf(this.method) == -1){
            throwException("IllegalArgument", "Unsupported method: " + this.method);
        }
        if(!this.path || typeof this.path !== "string"){
            throwException("IllegalArgument", "path must be a string. It was " + this.path);
        }
    }
}