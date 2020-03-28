(function Localization(){
    let additionalContent = {};

    window.Localization = new function(){
        this.getAdditionalContent = function(contentId){
            return additionalContent[contentId] || throwException("IllegalArgument", "No additionalContent found with id " + contentId);
        }
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType === events.LOAD_LOCALIZATION},
        function(pageName){
            loadLocalization(pageName.getPayload(), fillPageWithText);
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