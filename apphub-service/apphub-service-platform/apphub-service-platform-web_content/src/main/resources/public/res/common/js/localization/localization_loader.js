function loadLocalization(module, fileName, successCallback){
    const DEFAULT_LOCALE = "hu";

    return createQuery(
        module,
        fileName,
        getLocale(),
        successCallback,
        createQuery(
            module,
            fileName,
            getBrowserLanguage(),
            successCallback,
            createQuery(
                module,
                fileName,
                DEFAULT_LOCALE,
                successCallback,
                function(){throwException("ResourceNotFound", "Localization not found for fileName " + fileName)}
            )
        )
    )();

    function createQuery(module, fileName, locale, successCallback, errorCallback){
        return function(){
            const endpoint = new Endpoint(getPath(module, locale, fileName), HttpMethod.GET);

            let result;
            const request = new Request(endpoint);
                request.convertResponse = function(response){
                    return JSON.parse(response.body);
                }
                request.processValidResponse = function(localization){
                    result = successCallback(localization);
                }
                request.processInvalidResponse = function(){
                    if(errorCallback){
                        return errorCallback();
                    }else{
                        logService.log(response.toString(), "error", "Error loading localization: ");
                    }
                }
            dao.sendRequest(request);

            return result;
        }
    }

    function getPath(module, locale, fileName){
        return "/localization/module/" + module + "/" + locale + "/" + fileName + ".json";
    }
}