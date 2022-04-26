scriptLoader.loadScript("/res/community/js/contacts/contacts_controller.js");

(function PageController(){
    window.ids = {
    }

    window.pageController = new function(){

    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "community", fileName: "index"}));
    });
})();