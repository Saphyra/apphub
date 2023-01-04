scriptLoader.loadScript("/res/common/js/sync_engine.js");
scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/community/js/contacts/blacklist/create_blacklist_controller.js");

(function BlacklistController(){
    const syncEngine = new SyncEngineBuilder()
        .withContainerId(ids.contactsBlacklistList)
        .withGetKeyMethod((blacklist) => {return blacklist.blacklistId})
        .withCreateNodeMethod(createBlacklistNode)
        .withSortMethod((a, b) => {return a.username.localeCompare(b.username)})
        .withIdPrefix("blacklist")
        .build();

    window.blacklistController = new function(){
        this.load = load;
        this.addBlacklist = function(blacklist){
            syncEngine.add(blacklist);
        }
    }

    function load(){
        syncEngine.clear();

        const request = new Request(Mapping.getEndpoint("COMMUNITY_GET_BLACKLIST"));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(blacklists){
                syncEngine.addAll(blacklists);
            }
        dao.sendRequestAsync(request);
    }

    function createBlacklistNode(blacklist){
        const node = document.createElement("DIV");
            node.classList.add("list-item");
            node.classList.add("button");
            node.title = blacklist.email;

            const usernameSpan = document.createElement("SPAN");
                usernameSpan.innerText = blacklist.username;
        node.appendChild(usernameSpan);

            const buttonWrapper = document.createElement("DIV");
                buttonWrapper.classList.add("list-item-button-wrapper");

                const deleteBlacklistButton = document.createElement("BUTTON");
                    deleteBlacklistButton.innerText = localization.getAdditionalContent("delete-blacklist-button");
                    deleteBlacklistButton.onclick = function(){
                        deleteBlacklist(blacklist);
                    }
            buttonWrapper.appendChild(deleteBlacklistButton);

        node.appendChild(buttonWrapper);

        return node;
    }

    function deleteBlacklist(blacklist){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("delete-blacklist-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("delete-blacklist-confirmation-dialog-detail", {username: blacklist.username}))
            .withConfirmButton(localization.getAdditionalContent("delete-blacklist-confirmation-dialog-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("delete-blacklist-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "delete-blacklist-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("COMMUNITY_DELETE_BLACKLIST", {blacklistId: blacklist.blacklistId}));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(localization.getAdditionalContent("blacklist-deleted"));
                        syncEngine.remove(blacklist.blacklistId);

                    }
                dao.sendRequestAsync(request);
            }
        )
    }
})();