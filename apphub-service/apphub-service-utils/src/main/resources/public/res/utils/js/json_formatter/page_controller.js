(function PageController(){
    scriptLoader.loadScript("/res/utils/js/json_formatter/format_controller.js");
    scriptLoader.loadScript("/res/common/js/animation/move_controller.js");

    window.ids = {
        input: "input",
        resultContainer: "result",
    }

    window.pageController = new function(){

    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "utils", fileName: "json_formatter"}));
        addRightClickMove("result-container", "result");
        document.getElementById("result-container")
            .addEventListener('contextmenu', event => event.preventDefault());
    });
})();