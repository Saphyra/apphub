(function ContentController(){
    scriptLoader.loadScript("/res/common/js/confirmation_service.js");
    scriptLoader.loadScript("/res/notebook/js/content/category/category_content_controller.js");
    scriptLoader.loadScript("/res/notebook/js/content/category/category_node_factory.js");
    scriptLoader.loadScript("/res/notebook/js/content/text/text_content_controller.js");
    scriptLoader.loadScript("/res/notebook/js/content/text/text_node_factory.js");

    const nodeFactories = {
        CATEGORY: categoryNodeFactory,
        TEXT: textNodeFactory
    }

    window.contentController = new function(){
        this.createListItemId = createListItemId;
        this.nodeFactories = nodeFactories;
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){
            return eventType == events.LOCALIZATION_LOADED
        },
        function(){
            categoryContentController.loadCategoryContent(null)
        },
        true
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){
            return eventType == events.CATEGORY_DELETED
                || eventType == events.ITEM_DELETED
        },
        function(event){
            document.getElementById("category-content-list").removeChild(document.getElementById(createListItemId(event.getPayload())));
        },
    ));

    function createListItemId(listItemId){
        return "list-item-" + listItemId;
    }
})();