(function FriendController(){
    let searchForFriendTimeout = null;

    pageLoader.addLoader(function(){$("#" + ids.searchFriendInput).on("keyup", searchFriendAttempt)}, "SearchFriend input event listener");
    pageLoader.addLoader(loadFriendData, "Load friend data");
    pageLoader.addLoader(addWsHandlers, "Add WebSocket event handlers for Friendship");

    const incomingFriendRequestSyncEngine = new SyncEngineBuilder()
        .withContainerId(ids.incomingFriendRequestsContainer)
        .withGetKeyMethod((friendRequest) => {return friendRequest.friendRequestId})
        .withCreateNodeMethod(createIncomingFriendRequestNode)
        .withIdPrefix("incoming-friend-request")
        .withIfEmptyMethod(noResult)
        .withSortMethod((a, b) => {return a.senderName.localeCompare(b.senderName)})
        .build();

    const friendSyncEngine = new SyncEngineBuilder()
        .withContainerId(ids.friendListContainer)
        .withGetKeyMethod((friendship) => {return friendship.friendshipId})
        .withCreateNodeMethod(createFriendshipNode)
        .withIdPrefix("friend")
        .withIfEmptyMethod(noResult)
        .withSortMethod((a, b) => {return a.username.localeCompare(b.username)})
        .build();

    const sentFriendRequestSyncEngine = new SyncEngineBuilder()
        .withContainerId(ids.sentFriendRequestsContainer)
        .withGetKeyMethod((friendRequest) => {return friendRequest.friendRequestId})
        .withCreateNodeMethod(createSentFriendRequestNode)
        .withIdPrefix("sent-friend-request")
        .withIfEmptyMethod(noResult)
        .withSortMethod((a, b) => {return a.friendName.localeCompare(b.friendName)})
        .build();

    window.friendController = new function(){
    }

    function loadFriendData(){
        loadIncomingFriendRequests();
        loadFriends();
        loadSentFriendRequests();
    }

    function loadIncomingFriendRequests(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_INCOMING_FRIEND_REQUEST"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(friendRequests){
                incomingFriendRequestSyncEngine.clear();
                incomingFriendRequestSyncEngine.addAll(friendRequests);
            }
        dao.sendRequestAsync(request);
    }

    function createIncomingFriendRequestNode(friendRequest){
        const node = document.createElement("DIV");
            node.classList.add("friend-list-item");

            const friendName = document.createElement("DIV");
                friendName.innerText = friendRequest.senderName;
        node.appendChild(friendName);

            const buttonWrapper = document.createElement("DIV");
                buttonWrapper.classList.add("friend-list-button");

                const acceptButton = document.createElement("BUTTON");
                    acceptButton.innerHTML = localization.getAdditionalContent("accept-friend-request");
                    acceptButton.onclick = function(){
                        acceptFriendRequest(friendRequest.friendRequestId)
                    };
            buttonWrapper.appendChild(acceptButton);

                const cancelButton = document.createElement("BUTTON");
                    cancelButton.innerHTML = localization.getAdditionalContent("cancel-friend-request");
                    cancelButton.onclick = function(){
                        cancelFriendRequest(friendRequest.friendRequestId);
                    }
            buttonWrapper.appendChild(cancelButton);
        node.appendChild(buttonWrapper);
        return node;
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
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(friendRequest){
                document.getElementById(ids.searchFriendInput).value = "";
                $("#" + ids.friendSearchResultWrapper).fadeOut();
                notificationService.showSuccess(localization.getAdditionalContent("friend-request-sent"));
                sentFriendRequestSyncEngine.add(friendRequest);
            }
        dao.sendRequestAsync(request);
    }

    function loadFriends(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_FRIENDS"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(friendships){
                friendSyncEngine.clear();
                friendSyncEngine.addAll(friendships);
            }
        dao.sendRequestAsync(request);
    }

    function createFriendshipNode(friendship){
        const node = document.createElement("DIV");
            node.classList.add("friend-list-item");

            const friendName = document.createElement("DIV");
                friendName.innerText = friendship.friendName;
        node.appendChild(friendName);

            const removeButton = document.createElement("BUTTON");
                removeButton.classList.add("friend-list-button");
                removeButton.innerHTML = localization.getAdditionalContent("remove-friend");
                removeButton.onclick = function(){
                    removeFriend(friendship.friendshipId, friendship.friendName)
                }
        node.appendChild(removeButton);
        return node;
    }

    function loadSentFriendRequests(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_SENT_FRIEND_REQUEST"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(friendRequests){
                sentFriendRequestSyncEngine.clear();
                sentFriendRequestSyncEngine.addAll(friendRequests);
            }
        dao.sendRequestAsync(request);
    }

    function createSentFriendRequestNode(friendRequest){
        const node = document.createElement("DIV");
            node.classList.add("friend-list-item");

            const friendName = document.createElement("DIV");
                friendName.innerHTML = friendRequest.friendName;
        node.appendChild(friendName);

            const cancelButton = document.createElement("BUTTON");
                cancelButton.classList.add("friend-list-button");
                cancelButton.innerHTML = localization.getAdditionalContent("cancel-friend-request");
                cancelButton.onclick = function(){
                    cancelFriendRequest(friendRequest.friendRequestId);
                }
        node.appendChild(cancelButton);
        return node;
    }

    function noResult(){
        const noResult = document.createElement("DIV");
            noResult.classList.add("no-friend-list-item");
            noResult.innerHTML = localization.getAdditionalContent("empty-result");
        return noResult;
    }

    function cancelFriendRequest(friendRequestId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_CANCEL_FRIEND_REQUEST", {friendRequestId: friendRequestId}));
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("friend-request-canceled"));
                incomingFriendRequestSyncEngine.remove(friendRequestId);
                sentFriendRequestSyncEngine.remove(friendRequestId);
            }
        dao.sendRequestAsync(request);
    }

    function acceptFriendRequest(friendRequestId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_ACCEPT_FRIEND_REQUEST", {friendRequestId: friendRequestId}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(friendship){
                notificationService.showSuccess(localization.getAdditionalContent("friend-request-accepted"));
                incomingFriendRequestSyncEngine.remove(friendRequestId);
                friendSyncEngine.add(friendship);
            }
        dao.sendRequestAsync(request);
    }

    function removeFriend(friendshipId, friendName){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("remove-friend-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("remove-friend-confirmation-dialog-detail", {friendName: friendName}))
            .withConfirmButton(localization.getAdditionalContent("remove-friend-confirmation-dialog-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("remove-friend-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "remove-friend-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("SKYXPLORE_REMOVE_FRIEND", {friendshipId: friendshipId}));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(localization.getAdditionalContent("friend-removed"));
                        friendSyncEngine.remove(friendshipId);
                    }
                dao.sendRequestAsync(request);
            }
        )
    }

    function addWsHandlers(){
        wsConnection.addHandler(
            new WebSocketEventHandler(
                function(eventName){return eventName == "skyxplore-main-menu-friend-request-accepted"},
                (data) => {
                    sentFriendRequestSyncEngine.remove(data.friendRequestId);
                    friendSyncEngine.add(data.friendship);
                }
            )
        ).addHandler(
            new WebSocketEventHandler(
                function(eventName){return eventName == "skyxplore-main-menu-friend-request-sent"},
                function(friendRequest){
                    incomingFriendRequestSyncEngine.add(friendRequest);
                }
            )
        ).addHandler(
            new WebSocketEventHandler(
                function(eventName){return eventName == "skyxplore-main-menu-friend-request-deleted"},
                function(friendRequestId){
                    sentFriendRequestSyncEngine.remove(friendRequestId);
                    incomingFriendRequestSyncEngine.remove(friendRequestId);
                }
            )
        ).addHandler(
            new WebSocketEventHandler(
                function(eventName){return eventName == "skyxplore-main-menu-friendship-deleted"},
                (friendshipId) => friendSyncEngine.remove(friendshipId)
            )
        )
    }
})();