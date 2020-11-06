(function FriendController(){
    $(document).ready(init);

    let searchForFriendTimeout = null;

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
                node.innerHTML = character.name;
                node.onclick = function(){addFriend(character.id)};
            return node;
        }
    }

    function addFriend(characterId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_ADD_FRIEND"), {value: characterId});
            request.processValidResponse = function(){
                document.getElementById(ids.searchFriendInput).value = "";
                notificationService.showSuccess(Localization.getAdditionalContent("friend-request-sent"));
            }
        dao.sendRequestAsync(request);
    }

    function init(){
        $("#" + ids.searchFriendInput).on("keyup", searchFriendAttempt);
    }
})();