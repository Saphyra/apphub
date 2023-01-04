scriptLoader.loadScript("/res/common/js/sync_engine.js");

(function FriendRequestController(){
    const sentFriendRequestSyncEngine = new SyncEngineBuilder()
        .withContainerId(ids.sentFriendRequestsContent)
        .withGetKeyMethod((friendRequest) => {return friendRequest.friendRequestId})
        .withCreateNodeMethod(createSentFriendRequestNode)
        .withSortMethod((a, b) => {return a.username.localeCompare(b.username)})
        .withIdPrefix("sent-friend-request")
        .build();

    const receivedFriendRequestSyncEngine = new SyncEngineBuilder()
        .withContainerId(ids.receivedFriendRequestsContent)
        .withGetKeyMethod((friendRequest) => {return friendRequest.friendRequestId})
        .withCreateNodeMethod(createReceivedFriendRequestNode)
        .withSortMethod((a, b) => {return a.username.localeCompare(b.username)})
        .withIdPrefix("sent-friend-request")
        .build();

    window.friendRequestController = new function(){
        this.addSentFriendRequest = function(friendRequest){
            sentFriendRequestSyncEngine.add(friendRequest);
        }

        this.loadReceivedFriendRequests = loadReceivedFriendRequests;
        this.loadSentFriendRequests = loadSentFriendRequests;
    }

    function loadReceivedFriendRequests(){
        const request = new Request(Mapping.getEndpoint("COMMUNITY_GET_RECEIVED_FRIEND_REQUESTS"));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(friendRequests){
                receivedFriendRequestSyncEngine.clear();
                receivedFriendRequestSyncEngine.addAll(friendRequests);
            }
        dao.sendRequestAsync(request);
    }

    function loadSentFriendRequests(){
        const request = new Request(Mapping.getEndpoint("COMMUNITY_GET_SENT_FRIEND_REQUESTS"));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(friendRequests){
                sentFriendRequestSyncEngine.clear();
                sentFriendRequestSyncEngine.addAll(friendRequests);
            }
        dao.sendRequestAsync(request);
    }

    function createSentFriendRequestNode(friendRequest){
        const node = createBasicNode(friendRequest);

            const buttonWrapper = document.createElement("DIV");
                buttonWrapper.classList.add("list-item-button-wrapper");

                const deleteFriendRequestButton = document.createElement("BUTTON");
                    deleteFriendRequestButton.innerText = localization.getAdditionalContent("delete-friend-request-button");
                    deleteFriendRequestButton.onclick = function(){
                        deleteFriendRequest(friendRequest.friendRequestId, sentFriendRequestSyncEngine);
                    }
            buttonWrapper.appendChild(deleteFriendRequestButton);
        node.appendChild(buttonWrapper);

        return node;
    }

    function createReceivedFriendRequestNode(friendRequest){
        const node = createBasicNode(friendRequest);

            const buttonWrapper = document.createElement("DIV");
                buttonWrapper.classList.add("list-item-button-wrapper");

                const deleteFriendRequestButton = document.createElement("BUTTON");
                    deleteFriendRequestButton.innerText = localization.getAdditionalContent("delete-friend-request-button");
                    deleteFriendRequestButton.onclick = function(){
                        deleteFriendRequest(friendRequest.friendRequestId, receivedFriendRequestSyncEngine);
                    }
            buttonWrapper.appendChild(deleteFriendRequestButton);

                const acceptFriendRequestButton = document.createElement("BUTTON");
                    acceptFriendRequestButton.innerText = localization.getAdditionalContent("accept-friend-request-button");
                    acceptFriendRequestButton.onclick = function(){
                        acceptFriendRequest(friendRequest.friendRequestId);
                    }
            buttonWrapper.appendChild(acceptFriendRequestButton);
        node.appendChild(buttonWrapper);

        return node;
    }

    function createBasicNode(friendRequest){
        const node = document.createElement("DIV");
            node.classList.add("list-item");
            node.classList.add("button");
            node.title = friendRequest.email;

            const usernameSpan = document.createElement("SPAN");
                usernameSpan.innerText = friendRequest.username;
        node.appendChild(usernameSpan);
        return node;
    }

    function deleteFriendRequest(friendRequestId, syncEngine){
        const request = new Request(Mapping.getEndpoint("COMMUNITY_FRIEND_REQUEST_DELETE", {friendRequestId: friendRequestId}));
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("friend-request-deleted"));
                syncEngine.remove(friendRequestId);
            }
        dao.sendRequestAsync(request);
    }

    function acceptFriendRequest(friendRequestId){
        const request = new Request(Mapping.getEndpoint("COMMUNITY_FRIEND_REQUEST_ACCEPT", {friendRequestId: friendRequestId}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(friendship){
                notificationService.showSuccess(localization.getAdditionalContent("friend-request-accepted"));
                receivedFriendRequestSyncEngine.remove(friendRequestId);
                friendshipController.add(friendship);
            }
        dao.sendRequestAsync(request);
    }
})();