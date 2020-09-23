(function PageController(){
    scriptLoader.loadScript("/res/notebook/js/category_list_controller.js");
    scriptLoader.loadScript("/res/notebook/js/creation/category_creation_controller.js");
    scriptLoader.loadScript("/res/notebook/js/creation/text_creation_controller.js");
    scriptLoader.loadScript("/res/notebook/js/creation/link_creation_controller.js");
    scriptLoader.loadScript("/res/notebook/js/creation/checklist_creation_controller.js");
    scriptLoader.loadScript("/res/notebook/js/creation/table_creation_controller.js");
    scriptLoader.loadScript("/res/notebook/js/content/content_controller.js");
    scriptLoader.loadScript("/res/notebook/js/list_item_edition_service.js");

    events.OPEN_CREATE_CATEGORY_DIALOG = "OPEN_CREATE_CATEGORY_DIALOG";
    events.OPEN_CREATE_TEXT_DIALOG = "OPEN_CREATE_TEXT_DIALOG";
    events.OPEN_CREATE_LINK_DIALOG = "OPEN_CREATE_LINK_DIALOG";
    events.CATEGORY_DELETED = "CATEGORY_DELETED";
    events.ITEM_DELETED = "ITEM_DELETED";
    events.SAVE_CATEGORY = "save-category";
    events.CATEGORY_SAVED = "CATEGORY_SAVED";
    events.LIST_ITEM_SAVED = "LIST_ITEM_SAVED";
    events.OPEN_CREATE_CHECKLIST_DIALOG = "OPEN_CREATE_CHECKLIST_DIALOG";
    events.OPEN_CREATE_TABLE_DIALOG = "OPEN_CREATE_TABLE_DIALOG";

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "notebook", fileName: "notebook"}));
    });

    window.pageController = new function(){
        this.openCreateCategoryDialog = function(){
            document.getElementById("new-category-title").value = "";
            switchTab("main-page", "create-category");
        }

        this.openCreateTextDialog = function(){
            switchTab("main-page", "create-text");
        }

        this.openCreateLinkDialog = function(){
            switchTab("main-page", "create-link");
        }

        this.openCreateChecklistDialog = function(){
            switchTab("main-page", "create-checklist");
        }

        this.openCreateTableDialog = function(){
            switchTab("main-page", "create-table");
        }

        this.openMainPage = function(){
            switchTab("main-page", "main");
        }
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){
            return eventType == events.CATEGORY_SAVED
        },
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

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.OPEN_CREATE_LINK_DIALOG},
        pageController.openCreateLinkDialog
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.OPEN_CREATE_CHECKLIST_DIALOG},
        pageController.openCreateChecklistDialog
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.OPEN_CREATE_TABLE_DIALOG},
        pageController.openCreateTableDialog
    ));
})();