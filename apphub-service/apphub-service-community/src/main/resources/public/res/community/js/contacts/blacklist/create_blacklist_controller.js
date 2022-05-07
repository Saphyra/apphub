(function CreateBlacklistController(){
    pageLoader.addLoader(addEventListener, "Add eventListener to Create Blacklist search input");

    let timer = null;
    let lastQuery = null;

    window.createBlacklistController = new function(){
        this.openTab = openTab;
    }

    function openTab(){
        lastQuery = null;
        document.getElementById(ids.createBlacklistSearchInput).value = "";
        displayErrorQueryTooShort();
        switchTab("main-page", ids.createBlacklist);
    }

    function search(){
        const input = document.getElementById(ids.createBlacklistSearchInput).value;

        if(input.length < 3){
            displayErrorQueryTooShort();
            return;
        }

        if(lastQuery == input){
            return;
        }

        lastQuery = input;

        const request = new Request(Mapping.getEndpoint("COMMUNITY_BLACKLIST_SEARCH"), {value: input});
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(searchResult){
                displaySearchResult(searchResult);
            }
        dao.sendRequestAsync(request);

        function displaySearchResult(searchResult){
            if(searchResult.length == 0){
                displayErrorUserNotFound();
                return;
            }

            const container = document.getElementById(ids.createBlacklistSearchResult);
                container.innerHTML = "";

            new Stream(searchResult)
                .sorted((a, b) => {return a.username.localeCompare(b.username)})
                .map(createNode)
                .forEach((node) => {container.appendChild(node)});

            switchTab("create-blacklist-search-result", ids.createBlacklistSearchResult);

            function createNode(user){
                const item = document.createElement("DIV");
                    item.classList.add("button");

                    item.innerHTML = user.username + " / " + user.email;
                    item.onclick = function(){
                        createBlacklist(user.userId);
                    };

                return item;
            }
        }
    }

    function createBlacklist(userId){
        const request = new Request(Mapping.getEndpoint("COMMUNITY_CREATE_BLACKLIST"), {value: userId});
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(blacklist){
                blacklistController.addBlacklist(blacklist);
                notificationService.showSuccess(Localization.getAdditionalContent("blacklist-created"));
                pageController.displayMainPage();
            }
        dao.sendRequestAsync(request);
    }

    function displayErrorQueryTooShort(){
        switchTab("create-blacklist-search-result", ids.createBlacklistSearchResultQueryTooShort);
    }

    function displayErrorUserNotFound(){
        switchTab("create-blacklist-search-result", ids.createBlacklistSearchResultNoResult);
    }

    function addEventListener(){
        document.getElementById(ids.createBlacklistSearchInput).onkeyup = function(){
            if(timer){
                clearTimeout(timer);
            }

            timer = setTimeout(search, 1000);
        }
    }
})();