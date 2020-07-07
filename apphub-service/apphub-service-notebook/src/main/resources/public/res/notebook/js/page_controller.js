(function PageController(){
    scriptLoader.loadScript("/res/notebook/js/category_list_controller.js");
    scriptLoader.loadScript("/res/notebook/js/category_creation_controller.js");
    scriptLoader.loadScript("/res/notebook/js/content/category_view_controller.js");

    events.OPEN_CREATE_CATEGORY_DIALOG = "OPEN_CREATE_CATEGORY_DIALOG";
    events.CATEGORY_DELETED = "CATEGORY_DELETED";
    events.SAVE_CATEGORY = "save-category";
    events.CATEGORY_SAVED = "CATEGORY_SAVED";

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "notebook", fileName: "notebook"}));
    });

    window.pageController = new function(){
        this.openCreateCategoryDialog = function(){
            document.getElementById("new-category-title").value = "";
            switchTab("main-page", "create-category");
        }

        this.openMainPage = function(){
            switchTab("main-page", "main");
        }
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.CATEGORY_SAVED},
        pageController.openMainPage
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.OPEN_CREATE_CATEGORY_DIALOG},
        pageController.openCreateCategoryDialog
    ));
})();