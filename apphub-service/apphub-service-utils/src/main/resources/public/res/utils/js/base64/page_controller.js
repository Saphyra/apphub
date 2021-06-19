(function PageController(){
    scriptLoader.loadScript("/res/utils/js/base64/encode_controller.js");

    window.ids = {
        input: "input",
        result: "result",
    }

    window.pageController = new function(){

    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "utils", fileName: "base64"}));
    });
})();