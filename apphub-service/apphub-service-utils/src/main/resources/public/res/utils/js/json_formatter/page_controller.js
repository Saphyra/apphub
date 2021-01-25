(function PageController(){
    scriptLoader.loadScript("/res/utils/js/json_formatter/format_controller.js");

    window.ids = {
        input: "input",
        resultContainer: "result",
    }

    window.pageController = new function(){

    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "utils", fileName: "json_formatter"}));
    });
})();