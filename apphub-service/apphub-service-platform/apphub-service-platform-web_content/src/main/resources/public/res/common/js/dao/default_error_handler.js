(function DefaultErrorHandler(){
    window.defaultErrorHandler = new function(){
        this.handle = function(request, response){
            notificationService.showError("No errorHandler found for ErrorResponse: " + response.toString());
            if(typeof spinner !== "undefined"){
                spinner.close();
            }
        }
    }
})();