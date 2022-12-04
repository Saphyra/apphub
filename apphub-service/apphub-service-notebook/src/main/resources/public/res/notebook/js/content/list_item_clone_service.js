(function ListItemCloneService(){
    window.listItemCloneService = new function(){
        this.clone = clone;
    }

    function clone(listItemId, isCategory){
        const request = new Request(Mapping.getEndpoint("NOTEBOOK_CLONE_LIST_ITEM", {listItemId: listItemId}));
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("list-item-cloned"));
                eventProcessor.processEvent(new Event(isCategory ? events.CATEGORY_SAVED: events.LIST_ITEM_SAVED));
            }
        dao.sendRequestAsync(request);
    }
})();