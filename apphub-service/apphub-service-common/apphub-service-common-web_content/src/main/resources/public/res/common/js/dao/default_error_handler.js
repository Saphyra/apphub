(function DefaultErrorHandler(){
    window.defaultErrorHandler = new function(){
        this.handle = function(request, response){
            logService.log(response.toString(), "error", "No errorHandler found for ErrorResponse: ");
            spinner.close();
        }
    }
})();