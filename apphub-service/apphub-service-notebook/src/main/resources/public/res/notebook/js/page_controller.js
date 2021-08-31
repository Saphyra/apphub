scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/notebook/js/category_list_controller.js");
scriptLoader.loadScript("/res/notebook/js/creation/category_creation_controller.js");
scriptLoader.loadScript("/res/notebook/js/creation/text_creation_controller.js");
scriptLoader.loadScript("/res/notebook/js/creation/link_creation_controller.js");
scriptLoader.loadScript("/res/notebook/js/creation/checklist_creation_controller.js");
scriptLoader.loadScript("/res/notebook/js/creation/table_creation_controller.js");
scriptLoader.loadScript("/res/notebook/js/creation/checklist_table_creation_controller.js");
scriptLoader.loadScript("/res/notebook/js/content/list_item_clone_service.js");
scriptLoader.loadScript("/res/notebook/js/list_item_edition_service.js");
scriptLoader.loadScript("/res/notebook/js/pin_controller.js");
scriptLoader.loadScript("/res/notebook/js/search_controller.js");
scriptLoader.loadScript("/res/notebook/js/content/category/category_content_controller.js");
scriptLoader.loadScript("/res/notebook/js/content/category/category_node_factory.js")
scriptLoader.loadScript("/res/notebook/js/content/checklist/checklist_node_factory.js")
scriptLoader.loadScript("/res/notebook/js/content/table/table_node_factory.js")
scriptLoader.loadScript("/res/notebook/js/content/checklist_table/checklist_table_node_factory.js")
scriptLoader.loadScript("/res/notebook/js/content/action_button_factory.js")
scriptLoader.loadScript("/res/notebook/js/view/text_view_controller.js");
scriptLoader.loadScript("/res/notebook/js/content/link/link_node_factory.js");
scriptLoader.loadScript("/res/notebook/js/view/table_view_controller.js");
scriptLoader.loadScript("/res/notebook/js/content/text/text_node_factory.js");
scriptLoader.loadScript("/res/notebook/js/view/checklist_table_view_controller.js");
scriptLoader.loadScript("/res/notebook/js/view/checklist_view_controller.js");
scriptLoader.loadScript("/res/notebook/js/content/content_controller.js");

(function PageController(){
    events.CATEGORY_DELETED = "CATEGORY_DELETED";
    events.ITEM_DELETED = "ITEM_DELETED";
    events.CATEGORY_SAVED = "CATEGORY_SAVED";
    events.LIST_ITEM_SAVED = "LIST_ITEM_SAVED";

    window.ids = {
        pinnedItems: "pinned-items",
        searchInput: "search-container-title"
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "notebook", fileName: "notebook"}));
    });

    window.pageController = new function(){
        this.openMainPage = function(){
            switchTab("main-page", "category-content");
            switchTab("button-wrapper", "category-content-buttons");
        }
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){
            return eventType == events.CATEGORY_SAVED
        },
        pageController.openMainPage
    ));
})();