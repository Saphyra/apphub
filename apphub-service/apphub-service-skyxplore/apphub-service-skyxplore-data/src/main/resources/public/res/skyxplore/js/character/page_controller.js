scriptLoader.loadScript("/res/common/js/validation_util.js");
scriptLoader.loadScript("/res/skyxplore/js/character/character_loading_service.js");
scriptLoader.loadScript("/res/skyxplore/js/character/character_controller.js");

(function PageController(){
    window.ids = {
        characterNameInput: "character-name",
        characterTabTitle: "character-tab-title",
        submissionButton: "save-character",
        invalidName: "invalid-character-name"
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "skyxplore", fileName: "character"}));
    });

    window.pageController = new function(){
    }
})();