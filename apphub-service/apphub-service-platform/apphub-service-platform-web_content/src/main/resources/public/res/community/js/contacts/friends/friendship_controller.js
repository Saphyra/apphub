scriptLoader.loadScript("/res/common/js/sync_engine.js");
scriptLoader.loadScript("/res/common/js/confirmation_service.js");

(function FriendshipController(){
    const syncEngine = new SyncEngineBuilder()
        .withContainerId(ids.friendsListContent)
        .withGetKeyMethod((friendship) => {return friendship.friendshipId})
        .withCreateNodeMethod(createFriendshipNode)
        .withSortMethod((a, b) => {return a.username.localeCompare(b.username)})
        .withIdPrefix("friendship")
        .build();

    window.friendshipController = new function(){
        this.load = load;
        this.add = function(friendship){
            syncEngine.add(friendship);
        }
    }

    function load(){
        syncEngine.clear();

        const request = new Request(Mapping.getEndpoint("COMMUNITY_GET_FRIENDS"));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(friendships){
                syncEngine.addAll(friendships);
            }
        dao.sendRequestAsync(request);
    }

    function createFriendshipNode(friendship){
        const node = document.createElement("DIV");
            node.classList.add("list-item");
            node.classList.add("button");
            node.title = friendship.email;

            const usernameSpan = document.createElement("SPAN");
                usernameSpan.innerText = friendship.username;
        node.appendChild(usernameSpan);

            const buttonWrapper = document.createElement("DIV");
                buttonWrapper.classList.add("list-item-button-wrapper");

                const deleteFriendshipButton = document.createElement("BUTTON");
                    deleteFriendshipButton.innerText = localization.getAdditionalContent("delete-friendship-button");
                    deleteFriendshipButton.onclick = function(){
                        deleteFriendship(friendship);
                    }
            buttonWrapper.appendChild(deleteFriendshipButton);

        node.appendChild(buttonWrapper);

        return node;
    }

    function deleteFriendship(friendship){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("delete-friendship-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("delete-friendship-confirmation-dialog-detail", {username: friendship.username}))
            .withConfirmButton(localization.getAdditionalContent("delete-friendship-confirmation-dialog-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("delete-friendship-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "delete-friendship-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("COMMUNITY_DELETE_FRIENDSHIP", {friendshipId: friendship.friendshipId}));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(localization.getAdditionalContent("friendship-deleted"));
                        syncEngine.remove(friendship.friendshipId);

                    }
                dao.sendRequestAsync(request);
            }
        )
    }
})();