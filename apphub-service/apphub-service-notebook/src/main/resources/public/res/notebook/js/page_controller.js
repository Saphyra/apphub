(function PageController(){
    scriptLoader.loadScript("/res/notebook/js/category_list_controller.js");
    scriptLoader.loadScript("/res/notebook/js/category_creation_controller.js");
    scriptLoader.loadScript("/res/notebook/js/content/content_controller.js");

    events.OPEN_CREATE_CATEGORY_DIALOG = "OPEN_CREATE_CATEGORY_DIALOG";
    events.OPEN_CREATE_TEXT_DIALOG = "OPEN_CREATE_TEXT_DIALOG";
    events.CATEGORY_DELETED = "CATEGORY_DELETED";
    events.ITEM_DELETED = "ITEM_DELETED";
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

        this.openCreateTextDialog = function(){
            document.getElementById("create-text-selected-category-title").innerHTML = Localization.getAdditionalContent("root-title");
            document.getElementById("new-text-title").innerHTML = "";
            document.getElementById("new-text-content").innerHTML = "";
            switchTab("main-page", "create-text");
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

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.OPEN_CREATE_TEXT_DIALOG},
        pageController.openCreateTextDialog
    ));
})();