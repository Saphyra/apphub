(function FriendsController(){
    pageLoader.addLoader(loadActiveFriends, "Load active friends");

    window.friendsController = new function(){
        this.createCharacterOnlineHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return eventName == "skyxplore-lobby-user-online"},
                addActiveFriend
            );
        };

        this.createCharacterOfflineHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return eventName == "skyxplore-lobby-user-offline"},
                removeActiveFriend
            );
        };
    }

    function loadActiveFriends(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_ACTIVE_FRIENDS"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(friends){
                new Stream(friends)
                    .forEach(addActiveFriend);
            }
        dao.sendRequestAsync(request);
    }

    function addActiveFriend(friend){
        document.getElementById(ids.noActiveFriends).style.display = "none";

        const container = document.getElementById(ids.activeFriendsList);
            const node = document.createElement("DIV");
                node.id = createFriendNodeId(friend.friendId);
                node.classList.add("friend");
                node.classList.add("button");
                node.innerHTML = friend.friendName;
                node.onclick = function(){
                    inviteFriend(friend.friendId);
                }
        container.appendChild(node);
    }

    function removeActiveFriend(friend){
        const container = document.getElementById(ids.activeFriendsList);

            const friendNode = document.getElementById(createFriendNodeId(friend.friendId));
            if(friendNode){
                container.removeChild();
            }

            if(container.childNodes.length == 0){
                document.getElementById(ids.noActiveFriends).style.display = "block";
            }
    }

    function createFriendNodeId(friendId){
        return "friend-" + friendId;
    }

    function inviteFriend(friendId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_INVITE_TO_LOBBY", {friendId: friendId}));
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("friend-invited"));
            }
        dao.sendRequestAsync(request);
    }
})();