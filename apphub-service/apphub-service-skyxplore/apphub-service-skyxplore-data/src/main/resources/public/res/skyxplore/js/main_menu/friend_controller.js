(function FriendController(){
    scriptLoader.loadScript("/res/common/js/confirmation_service.js");

    let searchForFriendTimeout = null;

    $(document).ready(init);

    window.friendController = new function(){
        this.createHandlers = function(){
            return [
                new WebSocketEventHandler(
                    function(eventName){return eventName == "skyxplore-main-menu-friend-request-accepted"},
                    loadFriendData
                ),

                new WebSocketEventHandler(
                    function(eventName){return eventName == "skyxplore-main-menu-friend-request-sent"},
                    function(){
                        loadIncomingFriendRequests();
                        loadSentFriendRequests();
                    }
                ),

                new WebSocketEventHandler(
                    function(eventName){return eventName == "skyxplore-main-menu-friend-request-deleted"},
                    function(){
                        loadIncomingFriendRequests();
                        loadSentFriendRequests();
                    }
                ),

                new WebSocketEventHandler(
                    function(eventName){return eventName == "skyxplore-main-menu-friendship-deleted"},
                    loadFriends
                ),
            ];
        }
    }

    function loadFriendData(){
        loadIncomingFriendRequests();
        loadFriends();
        loadSentFriendRequests();
    }

    function searchFriendAttempt(){
        if(searchForFriendTimeout){
            clearTimeout(searchForFriendTimeout);
        }
        searchForFriendTimeout = setTimeout(searchFriend, 1000);
    }

    function searchFriend(){
        const searchResult = document.getElementById(ids.friendSearchResult);
            searchResult.innerHTML = "";

        const queryString = document.getElementById(ids.searchFriendInput).value;

        if(queryString.length == 0){
            console.log("Fading out searchResultContainer");
            $("#" + ids.friendSearchResultWrapper).fadeOut();
            return;
        }

        console.log("Fading in searchResultContainer");
        $("#" + ids.friendSearchResultWrapper).fadeIn();

        if(queryString.length < 3){
            console.log("Displaying message queryString too short");
            document.getElementById(ids.queryStringTooShort).style.display = "block";
            document.getElementById(ids.characterNotFound).style.display = "none";
            return;
        }

        document.getElementById(ids.queryStringTooShort).style.display = "none";
        document.getElementById(ids.characterNotFound).style.display = "none";

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_SEARCH_FOR_FRIENDS"), {value: queryString});
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(characters){
                if(characters.length == 0){
                    console.log("Displaying message no search result");
                    document.getElementById(ids.characterNotFound).style.display = "block";
                    return;
                }

                console.log("Displaying characters");
                new Stream(characters)
                    .map(createCharacterNode)
                    .forEach(function(node){searchResult.appendChild(node)});
            }
        dao.sendRequestAsync(request);

        function createCharacterNode(character){
            const node = document.createElement("DIV");
                node.classList.add("button");
                node.innerText = character.name;
                node.onclick = function(){addFriend(character.id)};
            return node;
        }
    }

    function addFriend(characterId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_ADD_FRIEND"), {value: characterId});
            request.processValidResponse = function(){
                document.getElementById(ids.searchFriendInput).value = "";
                $("#" + ids.friendSearchResultWrapper).fadeOut();
                notificationService.showSuccess(Localization.getAdditionalContent("friend-request-sent"));
                loadFriendData();
            }
        dao.sendRequestAsync(request);
    }

    function loadIncomingFriendRequests(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_INCOMING_FRIEND_REQUEST"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = displayIncomingFriendRequests
        dao.sendRequestAsync(request);

        function displayIncomingFriendRequests(friendRequests){
            const container = document.getElementById(ids.incomingFriendRequestsContainer);
                container.innerHTML = "";

                if(friendRequests.length == 0){
                    const noResult = document.createElement("DIV");
                        noResult.classList.add("no-friend-list-item");
                        noResult.innerHTML = Localization.getAdditionalContent("empty-result");
                    container.appendChild(noResult);
                }

            new Stream(friendRequests)
                .map(createNode)
                .forEach(function(node){container.appendChild(node)});

            function createNode(friendRequest){
                const node = document.createElement("DIV");
                    node.classList.add("friend-list-item");

                    const friendName = document.createElement("DIV");
                        friendName.innerText = friendRequest.senderName;
                node.appendChild(friendName);

                    const buttonWrapper = document.createElement("DIV");
                        buttonWrapper.classList.add("friend-list-button");

                        const acceptButton = document.createElement("BUTTON");
                            acceptButton.innerHTML = Localization.getAdditionalContent("accept-friend-request");
                            acceptButton.onclick = function(){
                                acceptFriendRequest(friendRequest.friendRequestId)
                            };
                    buttonWrapper.appendChild(acceptButton);

                        const cancelButton = document.createElement("BUTTON");
                            cancelButton.innerHTML = Localization.getAdditionalContent("cancel-friend-request");
                            cancelButton.onclick = function(){
                                cancelFriendRequest(friendRequest.friendRequestId);
                            }
                    buttonWrapper.appendChild(cancelButton);
                node.appendChild(buttonWrapper);
                return node;
            }
        }
    }

    function loadFriends(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_FRIENDS"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = displaySentFriendRequests
        dao.sendRequestAsync(request);

        function displaySentFriendRequests(friends){
            const container = document.getElementById(ids.friendListContainer);
                container.innerHTML = "";

                if(friends.length == 0){
                    const noResult = document.createElement("DIV");
                        noResult.classList.add("no-friend-list-item");
                        noResult.innerHTML = Localization.getAdditionalContent("empty-result");
                    container.appendChild(noResult);
                }

            new Stream(friends)
                .map(createNode)
                .forEach(function(node){container.appendChild(node)});

            function createNode(friend){
                const node = document.createElement("DIV");
                    node.classList.add("friend-list-item");

                    const friendName = document.createElement("DIV");
                        friendName.innerText = friend.friendName;
                node.appendChild(friendName);

                    const removeButton = document.createElement("BUTTON");
                        removeButton.classList.add("friend-list-button");
                        removeButton.innerHTML = Localization.getAdditionalContent("remove-friend");
                        removeButton.onclick = function(){
                            removeFriend(friend.friendshipId, friend.friendName)
                        }
                node.appendChild(removeButton);
                return node;
            }
        }
    }

    function loadSentFriendRequests(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_SENT_FRIEND_REQUEST"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = displaySentFriendRequests
        dao.sendRequestAsync(request);

        function displaySentFriendRequests(friendRequests){
            const container = document.getElementById(ids.sentFriendRequestsContainer);
                container.innerHTML = "";

                if(friendRequests.length == 0){
                    const noResult = document.createElement("DIV");
                        noResult.classList.add("no-friend-list-item");
                        noResult.innerHTML = Localization.getAdditionalContent("empty-result");
                    container.appendChild(noResult);
                }

            new Stream(friendRequests)
                .map(createNode)
                .forEach(function(node){container.appendChild(node)});

            function createNode(friendRequest){
                const node = document.createElement("DIV");
                    node.classList.add("friend-list-item");

                    const friendName = document.createElement("DIV");
                        friendName.innerHTML = friendRequest.friendName;
                node.appendChild(friendName);

                    const cancelButton = document.createElement("BUTTON");
                        cancelButton.classList.add("friend-list-button");
                        cancelButton.innerHTML = Localization.getAdditionalContent("cancel-friend-request");
                        cancelButton.onclick = function(){
                            cancelFriendRequest(friendRequest.friendRequestId);
                        }
                node.appendChild(cancelButton);
                return node;
            }
        }
    }

    function cancelFriendRequest(friendRequestId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_CANCEL_FRIEND_REQUEST", {friendRequestId: friendRequestId}));
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("friend-request-canceled"));
                loadFriendData();
            }
        dao.sendRequestAsync(request);
    }

    function acceptFriendRequest(friendRequestId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_ACCEPT_FRIEND_REQUEST", {friendRequestId: friendRequestId}));
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("friend-request-accepted"));
                loadFriendData();
            }
        dao.sendRequestAsync(request);
    }

    function removeFriend(friendshipId, friendName){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("remove-friend-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("remove-friend-confirmation-dialog-detail", {friendName: friendName}))
            .withConfirmButton(Localization.getAdditionalContent("remove-friend-confirmation-dialog-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("remove-friend-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "remove-friend-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("SKYXPLORE_REMOVE_FRIEND", {friendshipId: friendshipId}));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(Localization.getAdditionalContent("friend-removed"));
                        loadFriendData();
                    }
                dao.sendRequestAsync(request);
            }
        )
    }

    function init(){
        $("#" + ids.searchFriendInput).on("keyup", searchFriendAttempt);
        loadFriendData();
    }
})();