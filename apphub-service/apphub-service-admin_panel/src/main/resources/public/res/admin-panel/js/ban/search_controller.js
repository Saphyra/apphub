(function SearchController(){
    pageLoader.addLoader(search, "Init displayed SearchResult");

    window.searchController = new function(){
        this.search = search;
    }

    function search(){
        const searchText = document.getElementById(ids.search).value;

        if(searchText.length < 3){
            switchTab("search-result", ids.searchTextTooShort);
            return;
        }

        const request = new Request(Mapping.getEndpoint("USER_DATA_SEARCH_ACCOUNT", {}, {includeMarkedForDeletion: true}), {value: searchText});
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(users){
                if(users.length == 0){
                    switchTab("search-result", ids.noResult);
                    return;
                }

                const container = document.getElementById(ids.searchResultContent);
                    container.innerHTML = "";

                    new Stream(users)
                        .map(mapUser)
                        .forEach(function(node){container.appendChild(node)});

                switchTab("search-result", ids.searchResult);

                function mapUser(user){
                    const node = document.createElement("TR");
                        const userIdCell = document.createElement("TD");
                            userIdCell.innerText = user.userId;
                    node.appendChild(userIdCell);

                        const emailCell = document.createElement("TD");
                            emailCell.innerText = user.email;
                    node.appendChild(emailCell);

                        const usernameCell = document.createElement("TD");
                            usernameCell.innerText = user.username;
                    node.appendChild(usernameCell);

                    node.onclick = function(){
                        banController.openUser(user.userId);
                    }
                    return node;
                }
            }
        dao.sendRequestAsync(request);
    }
})();