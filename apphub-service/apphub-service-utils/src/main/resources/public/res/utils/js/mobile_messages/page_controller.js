(function PageController(){
    scriptLoader.loadScript("/res/utils/js/mobile_messages/sql_generator.js");

    window.ids = {
        resultContainer: "result",
        messageTypeInput: "message-type-input",
        osInput: "os-input",
        enTitleInput: "en-title-input",
        enDetailInput: "en-detail-input",
        deTitleInput: "de-title-input",
        deDetailInput: "de-detail-input",
    }

    window.pageController = new function(){

    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "utils", fileName: "mobile_messages"}));
    });
})();