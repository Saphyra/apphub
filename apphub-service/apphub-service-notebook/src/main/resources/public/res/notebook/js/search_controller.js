(function SearchController(){
    let timeout = null;
    let shouldReset = false;

    window.searchController = new function(){
        this.resetSearchText = resetSearchText;
    }

    pageLoader.addLoader(function(){document.getElementById(ids.searchInput).onkeyup = search}, "Search onclick event listener");

    function search(){
        if(timeout){
            clearTimeout(timeout);
        }

        timeout = setTimeout(searchAttempt, 1000);
    }

    function searchAttempt(){
        const searchText = document.getElementById(ids.searchInput).value;
        if(searchText.length < 3){
            if(shouldReset){
                categoryContentController.reloadCategoryContent();
            }
            shouldReset = false;
            return;
        }

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_SEARCH"), {value: searchText});
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(items){
                shouldReset = true;
                categoryContentController.displayCategoryDetails(null, {title: Localization.getAdditionalContent("search-result"), children: items});
            }
        dao.sendRequestAsync(request);
    }

    function resetSearchText(){
        document.getElementById(ids.searchInput).value = "";
        categoryContentController.reloadCategoryContent();
        shouldReset = false;
    }
})();