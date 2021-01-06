(function Localization(){
    let additionalContent = {};

    window.Localization = new function(){
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

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType === events.LOAD_LOCALIZATION},
        function(event){
            const payload = event.getPayload();
            const shouldFillPageWithText = payload.shouldFillPageWithText != false;
            const callback = shouldFillPageWithText ?
                fillPageWithText
                : function(content){
                    additionalContent = content.additionalContent;
                    eventProcessor.processEvent(new Event(events.LOCALIZATION_LOADED));
                }

            loadLocalization(payload.module, payload.fileName, callback);
        },
        true
    ));
    
    function fillPageWithText(content){
        document.title = content.title;
        for(let id in content.staticText){
            const element = document.getElementById(id);
            if(element){
                const localizations = content.staticText[id];
                for(let lindex in localizations){
                    element[localizations[lindex].key] = localizations[lindex].message;
                }
            }else logService.log("Element not found with id " + id, "warn");
        }
        additionalContent = content.additionalContent;
        eventProcessor.processEvent(new Event(events.LOCALIZATION_LOADED));
    }
})();