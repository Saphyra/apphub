(function PageController(){
    scriptLoader.loadScript("/res/utils/js/mobile_messages/sql_generator.js");

    window.ids = {
        resultContainer: "result",
        messageTypeInput: "message-type-input",
        osInput: "os-input",
        enTitleInput: "en-title",
        enDetailInput: "en-detail",
        deTitleInput: "de-title",
        deDetailInput: "de-detail",
        showContact: "show-contact",
        version: "version-input",
    }

    window.pageController = new function(){

    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "utils", fileName: "mobile_messages"}));
    });
})();