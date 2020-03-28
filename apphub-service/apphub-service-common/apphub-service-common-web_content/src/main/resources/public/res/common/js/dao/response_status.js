(function ResponseStatusMapper(){
    window.responseStatusMapper = new function(){
        this.getKeyOf = getKeyOf;
    }
    
    /*
    Gets the key of the given status code.
    Arguments: 
        - statusCode: the status code
    Returns:
        - The key of the given status code
    Throws:
        - IllegalArgument exception if statusCode is undefined.
        - KeyNotFound exception if key not found.
    */
    function getKeyOf(statusCode){
        try{
            if(statusCode == undefined){
                throwException("IllegalArgument", "statusCode must not be null or undefined");
            }
            if(statusCode == null){
                return null;
            }
            if(typeof statusCode != "number"){
                throwException("IllegalArgument", "statusCode must be a number");
            }
            
            for(let key in ResponseStatus){
                if(ResponseStatus[key] == statusCode){
                    return key;
                }
            }
            
            throwException("KeyNotFound", "No key found for status code " + statusCode);
        }catch(err){
            const message = arguments.callee.name + " - " + err.name + ": " + err.message;
            logService.log(message, "error");
            return null;
        }
    }
})();

/*
Enumeration contains response status codes for HttpRequest
*/
window.ResponseStatus = new function(){
    this.OK = 200;
    this.BAD_REQUEST = 400;
    this.UNAUTHORIZED = 401;
    this.FORBIDDEN = 403
    this.NOT_FOUND = 404;
    this.METHOD_NOT_ALLOWED = 405;
    this.CONFLICT = 409;
    this.GONE = 410;
    this.PRECONDITION_FAILED = 412;
    this.LOCKED = 423;
    this.TOO_MANY_REQUESTS = 429
    this.INTERNAL_SERVER_ERROR = 500;
    this.NOT_IMPLEMENTED = 501;
    this.CONNECTION_REFUSED = 0;
}