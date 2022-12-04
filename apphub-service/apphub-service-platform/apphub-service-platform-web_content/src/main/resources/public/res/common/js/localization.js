(function Localization(){
    let additionalContent = {};

    window.localization = new function(){
        this.loadLocalization = function(module, fileName){
            const content = loadLocalization(module, fileName);
            additionalContent = content.additionalContent;
            fillPageWithText(content);

            pageLoader.runLoaders();
            notificationService.printStoredMessages();
        };
        this.loadCustomLocalization = function(module, fileName){
            return new CustomLocalization(module, fileName);
        }
        this.getAdditionalContent = function(contentId, variables){
            let result = additionalContent[contentId] || "No additionalContent found with id " + contentId;

            new MapStream(variables)
                .forEach(function(search, value){
                    const key = "{" + search + "}";
                    result = result.replace(key, value);
                });

            return result;
        }
    }
    
    function fillPageWithText(content){
        document.title = content.title;
        for(let id in content.staticText){
            const element = document.getElementById(id);
            if(element){
                const localizations = content.staticText[id];
                for(let lindex in localizations){
                    element[localizations[lindex].key] = localizations[lindex].message;
                }
            }else console.log("Element not found with id " + id);
        }
        additionalContent = content.additionalContent;
    }

    function loadLocalization(module, fileName){
        module || throwException("IllegalArgument", "module must not be null");
        fileName || throwException("IllegalArgument", "fileName must not be null");

        const locales = [getLocale(), getBrowserLanguage(), DEFAULT_LOCALE];

        for(let i in locales){
            const locale = locales[i];
            let result = null;

            const endpoint = new Endpoint(getPath(module, locale, fileName), HttpMethod.GET);

            const request = new Request(endpoint);
                request.convertResponse = jsonConverter;
                request.processValidResponse = function(localization){
                    result = localization;
                }
                request.processInvalidResponse = function(){
                    console.log(response.toString(), "error", "Error loading localization: ");
                }
            dao.sendRequest(request);

            if(result != null){
                return result;
            }
        }

        throwException("ResourceNotFound", "Localization not found for fileName " + fileName);

        function getPath(module, locale, fileName){
            return "/localization/module/" + module + "/" + locale + "/" + fileName + ".json?date=" + getDate();
        }

        function getDate(){
            const date = new Date();
            return date.getFullYear() + "-" + date.getMonth() + "-" + date.getDate() + "/" + date.getHours() + ":" + date.getMinutes();
        }
    }

    function CustomLocalization(module, fileName){
        console.log("Initializing CustomLocalization for module " + module + " and fileName " + fileName);

        const localization = loadLocalization(module, fileName);

        this.getKeys = function(){
            return Object.keys(localization);
        }

        this.get = function(key){
            return localization[key] || function(){
                const message = "Localization not found with key " + key + " in file " + fileName;
                console.error(message);
                return message;
            }()
        }
    }
})();