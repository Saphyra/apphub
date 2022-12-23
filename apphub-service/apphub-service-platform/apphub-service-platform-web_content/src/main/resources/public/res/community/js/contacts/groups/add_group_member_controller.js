(function AddGroupMemberController(){
    pageLoader.addLoader(addEventListener, "Add eventListener to Group AddMember search input");

    let timer = null;
    let lastQuery = null;

    window.addGroupMemberController = new function(){
        this.openAddGroupMemberWindow = openAddGroupMemberWindow;
    }

    function openAddGroupMemberWindow(){
        lastQuery = null;
        document.getElementById(ids.groupAddMemberSearchInput).value = "";
        displayErrorQueryTooShort();
        switchTab("main-page", ids.groupAddMember);
    }

    function search(){
        const input = document.getElementById(ids.groupAddMemberSearchInput).value;

        if(input.length < 3){
            displayErrorQueryTooShort();
            return;
        }

        if(lastQuery == input){
            return;
        }

        lastQuery = input;

        const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_SEARCH_MEMBER_CANDIDATES", {groupId: groupDetailsController.getCurrentGroup().groupId}), {value: input});
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

            const container = document.getElementById(ids.groupAddMemberSearchResult);
                container.innerHTML = "";

            new Stream(searchResult)
                .sorted((a, b) => {return a.username.localeCompare(b.username)})
                .map(createNode)
                .forEach((node) => {container.appendChild(node)});

            switchTab("group-add-member-search-result", ids.groupAddMemberSearchResult);

            function createNode(user){
                const item = document.createElement("DIV");
                    item.classList.add("button");

                    item.innerHTML = user.username + " / " + user.email;
                    item.onclick = function(){
                        addMember(user.userId);
                    };

                return item;
            }
        }
    }

    function addMember(userId){
        const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_CREATE_MEMBER", {groupId: groupDetailsController.getCurrentGroup().groupId}), {value: userId});
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(groupMember){
                notificationService.showSuccess(localization.getAdditionalContent("group-member-added"));
                groupDetailsController.addMember(groupMember);
                groupDetailsController.openWindow();
            }
        dao.sendRequestAsync(request);
    }

    function displayErrorQueryTooShort(){
        switchTab("group-add-member-search-result", ids.groupAddMemberSearchResultQueryTooShort);
    }

    function displayErrorUserNotFound(){
        switchTab("group-add-member-search-result", ids.groupAddMemberSearchResultNoResult);
    }

    function addEventListener(){
        document.getElementById(ids.groupAddMemberSearchInput).onkeyup = function(){
            if(timer){
                clearTimeout(timer);
            }

            timer = setTimeout(search, 1000);
        }
    }
})();