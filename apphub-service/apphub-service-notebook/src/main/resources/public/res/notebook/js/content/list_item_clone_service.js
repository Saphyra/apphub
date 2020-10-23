(function ListItemCloneService(){
    window.listItemCloneService = new function(){
        this.clone = clone;
    }

    function clone(listItemId, isCategory){
        const request = new Request(Mapping.getEndpoint("CLONE_NOTEBOOK_LIST_ITEM", {listItemId: listItemId}));
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("list-item-cloned"));
                eventProcessor.processEvent(new Event(isCategory ? events.CATEGORY_SAVED: events.LIST_ITEM_SAVED));
            }
        dao.sendRequestAsync(request);
    }
})();