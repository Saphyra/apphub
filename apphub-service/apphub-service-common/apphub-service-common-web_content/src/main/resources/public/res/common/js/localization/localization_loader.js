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
            const response = dao.sendRequest(HttpMethod.GET, getPath(module, locale, fileName));
            if(response.status === ResponseStatus.OK){
                return successCallback(JSON.parse(response.body));
            }else if(errorCallback){
                return errorCallback();
            }else{
                logService.log(response.toString(), "error", "Error loading localization: ");
            }
        }
    }

    function getPath(module, locale, fileName){
        return "/localization/module/" + module + "/" + locale + "/" + fileName + ".json";
    }
}