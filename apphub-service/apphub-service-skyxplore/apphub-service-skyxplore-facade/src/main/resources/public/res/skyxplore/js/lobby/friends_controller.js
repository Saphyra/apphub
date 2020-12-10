(function FriendsController(){
    let refreshInterval = null;

    $(document).ready(init);

    function setRefreshInterval(){
        if(refreshInterval){
            clearInterval(refreshInterval);
        }

        refreshInterval = setInterval(loadActiveFriends, 5000);
    }

    function loadActiveFriends(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_ACTIVE_FRIENDS")); //TODO move to webSocket
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(friends){
                displayFriends(friends);
            }
        dao.sendRequestAsync(request);

        function displayFriends(friends){
            const container = document.getElementById(ids.friendsList);
                container.innerHTML = "";

                if(friends.length == 0){
                    const noFriend = document.createElement("DIV");
                        noFriend.classList.add("friend");
                        noFriend.innerHTML = Localization.getAdditionalContent("no-active-friends");
                    container.appendChild(noFriend);
                }

            new Stream(friends)
                .map(createNode)
                .forEach(function(node){container.appendChild(node)});

            function createNode(friend){
                const node = document.createElement("DIV");
                    node.classList.add("friend");
                    node.classList.add("button");
                    node.innerHTML = friend.friendName;
                    node.onclick = function(){
                        inviteFriend(friend.friendId);
                    }
                return node;
            }
        }
    }

    function inviteFriend(friendId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_INVITE_TO_LOBBY", {friendId: friendId}));
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("friend-invited"));
            }
        dao.sendRequestAsync(request);
    }

    function init(){
        loadActiveFriends();
        document.getElementById(ids.friendsContainer).onmousemove = setRefreshInterval;
        setRefreshInterval();
    }
})();