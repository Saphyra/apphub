(function CreateFriendRequestController(){
    pageLoader.addLoader(addEventListener, "Add eventListener to Create FriendRequest search input");

    let timer = null;
    let lastQuery = null;

    window.createFriendRequestController = new function(){
        this.openTab = openTab;
    }

    function openTab(){
        lastQuery = null;
        document.getElementById(ids.createFriendRequestSearchInput).value = "";
        displayErrorQueryTooShort();
        switchTab("main-page", ids.createFriendRequest);
    }

    function search(){
        const input = document.getElementById(ids.createFriendRequestSearchInput).value;

        if(input.length < 3){
            displayErrorQueryTooShort();
            return;
        }

        if(lastQuery == input){
            return;
        }

        lastQuery = input;

        const request = new Request(Mapping.getEndpoint("COMMUNITY_FRIEND_REQUEST_SEARCH"), {value: input});
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

            const container = document.getElementById(ids.createFriendRequestSearchResult);
                container.innerHTML = "";

            new Stream(searchResult)
                .sorted((a, b) => {return a.username.localeCompare(b.username)})
                .map(createNode)
                .forEach((node) => {container.appendChild(node)});

            switchTab("create-friend-request-search-result", ids.createFriendRequestSearchResult);

            function createNode(user){
                const item = document.createElement("DIV");
                    item.classList.add("button");

                    item.innerHTML = user.username + " / " + user.email;
                    item.onclick = function(){
                        createFriendRequest(user.userId)
                    };

                return item;
            }
        }
    }

    function createFriendRequest(userId){
        const request = new Request(Mapping.getEndpoint("COMMUNITY_FRIEND_REQUEST_CREATE"), {value: userId});
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(friendRequest){
                friendRequestController.addSentFriendRequest(friendRequest);
                notificationService.showSuccess(Localization.getAdditionalContent("friend-request-created"));
                pageController.displayMainPage();
            }
        dao.sendRequestAsync(request);
    }

    function displayErrorQueryTooShort(){
        switchTab("create-friend-request-search-result", ids.createFriendRequestSearchResultQueryTooShort);
    }

    function displayErrorUserNotFound(){
        switchTab("create-friend-request-search-result", ids.createFriendRequestSearchResultNoResult);
    }

    function addEventListener(){
        document.getElementById(ids.createFriendRequestSearchInput).onkeyup = function(){
            if(timer){
                clearTimeout(timer);
            }

            timer = setTimeout(search, 1000);
        }
    }
})();