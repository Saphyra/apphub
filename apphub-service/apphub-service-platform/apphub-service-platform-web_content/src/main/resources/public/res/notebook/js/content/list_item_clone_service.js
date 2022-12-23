(function ListItemCloneService(){
    window.listItemCloneService = new function(){
        this.clone = clone;
    }

    function clone(listItemId, isCategory){
        const request = new Request(Mapping.getEndpoint("NOTEBOOK_CLONE_LIST_ITEM", {listItemId: listItemId}));
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("list-item-cloned"));
                categoryTreeController.reloadCategories();
                categoryContentController.reloadCategoryContent();
            }
        dao.sendRequestAsync(request);
    }
})();