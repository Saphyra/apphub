scriptLoader.loadScript("/res/common/js/sync_engine.js");
scriptLoader.loadScript("/res/common/js/confirmation_service.js");

(function GroupDetailsController(){
    pageLoader.addLoader(addEventListeners, "Add eventListeners to Group Details window");

    let currentGroup;
    let ownMember;

    const syncEngine = new SyncEngineBuilder()
        .withContainerId(ids.groupDetailsMembers)
        .withGetKeyMethod((groupMember) => {return groupMember.groupMemberId})
        .withCreateNodeMethod(createGroupMemberNode)
        .withSortMethod((a, b) => {
            if(a.userId == currentGroup.ownerId){
                return -1;
            }

            if(b.userId == currentGroup.ownerId){
                return 1;
            }

            return a.username.localeCompare(b.username)
        })
        .withIdPrefix("group-member")
        .build();

    window.groupDetailsController = new function(){
        this.openGroup = openGroup;
        this.openCurrentGroup = function(){
            openGroup(currentGroup);
        }
        this.getCurrentGroup = function(){
            return currentGroup;
        }
        this.addMember = function(groupMember){
            syncEngine.add(groupMember);
        }
        this.openWindow = function(){
            switchTab("main-page", ids.groupDetails);
        }
    }

    function openGroup(group){
        currentGroup = group;

        document.getElementById(ids.groupDetailsAddMemberButton).disabled = true;

        const title = document.getElementById(ids.groupDetailsTitle);
            title.contentEditable = group.ownerId == window.userId;
            title.innerText = group.name;

        const invitationTypeSelect = document.getElementById(ids.groupDetailsInvitationType);
            invitationTypeSelect.disabled = group.ownerId != window.userId;
            invitationTypeSelect.value = group.invitationType;

        loadMembers(group.groupId);

        switchTab("main-page", ids.groupDetails);

        function loadMembers(groupId){
            syncEngine.clear();

            const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_GET_MEMBERS", {groupId: groupId}));
                request.convertResponse = jsonConverter;
                request.processValidResponse = function(groupMembers){
                    ownMember = new Stream(groupMembers)
                       .filter((groupMember) => {return groupMember.userId == window.userId})
                       .findFirst()
                       .orElseThrow("IllegalState", window.userId + " is not member of group " + groupId);

                    let canInvite = currentGroup.ownerId == window.userId || ownMember.canInvite;

                    document.getElementById(ids.groupDetailsAddMemberButton).disabled = !canInvite;

                    syncEngine.addAll(groupMembers);
                }
            dao.sendRequestAsync(request);
        }
    }

    function createGroupMemberNode(groupMember){
        const node = document.createElement("TR");
            node.title = groupMember.username;

            const usernameCell = document.createElement("TD");
                usernameCell.innerText = groupMember.username;
        node.appendChild(usernameCell);

            const canInviteCell = document.createElement("TD");
                const canInviteInput = document.createElement("INPUT");
                    canInviteInput.type = "checkbox";
                    canInviteInput.checked = groupMember.canInvite;
                    canInviteInput.disabled = !canOperate(groupMember, ownMember.canModifyRoles);
            canInviteCell.appendChild(canInviteInput);
        node.appendChild(canInviteCell);

            const canKickCell = document.createElement("TD");
                const canKickInput = document.createElement("INPUT");
                    canKickInput.type = "checkbox";
                    canKickInput.checked = groupMember.canKick;
                    canKickInput.disabled = !canOperate(groupMember, ownMember.canModifyRoles);
            canKickCell.appendChild(canKickInput);
        node.appendChild(canKickCell);

            const canModifyRolesCell = document.createElement("TD");
                const canModifyRolesInput = document.createElement("INPUT");
                    canModifyRolesInput.type = "checkbox";
                    canModifyRolesInput.checked = groupMember.canModifyRoles;
                    canModifyRolesInput.disabled = !canOperate(groupMember, ownMember.canModifyRoles);
            canModifyRolesCell.appendChild(canModifyRolesInput);
        node.appendChild(canModifyRolesCell);

            const kickCell = document.createElement("TD");
                const kickButton = document.createElement("BUTTON");
                    kickButton.innerText = "X";
                    kickButton.disabled = !canOperate(groupMember, ownMember.canKick);
            kickCell.appendChild(kickButton);
        node.appendChild(kickCell);


        canInviteInput.onchange = function(){
            updateGroupMember(groupMember, canInviteInput, canKickInput, canModifyRolesInput);
        }
        canKickInput.onchange = function(){
             updateGroupMember(groupMember, canInviteInput, canKickInput, canModifyRolesInput);
        }
        canModifyRolesCell.onchange = function(){
            updateGroupMember(groupMember, canInviteInput, canKickInput, canModifyRolesInput);
        }

        kickButton.onclick = function(){
            kickMember(groupMember)
        };

        return node;

        function canOperate(groupMember, setting){
            if(groupMember.userId == currentGroup.ownerId){
                console.log("Group member is the owner", groupMember);
                return false;
            }

            console.log(setting);
            return setting;
        }
    }

    function updateName(){
        const titleNode = document.getElementById(ids.groupDetailsTitle);
        const name = titleNode.innerText;

        if(name == currentGroup.name){
            return;
        }

        if(name.length < 3){
            notificationService.showError(Localization.getAdditionalContent("group-name-too-short"));
            titleNode.innerText = currentGroup.name;
            return;
        }

        if(name.length > 30){
            notificationService.showError(Localization.getAdditionalContent("group-name-too-long"));
            titleNode.innerText = currentGroup.name;
            return;
        }

        const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_RENAME", {groupId: currentGroup.groupId}), {value: name});
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(group){
                groupsController.addGroup(group);
                notificationService.showSuccess(Localization.getAdditionalContent("group-renamed"));
            }
        dao.sendRequestAsync(request);
    }

    function updateGroupMember(groupMember, canInviteInput, canKickInput, canModifyRolesInput){
        //TODO
    }

    function changeInvitationType(){
        const invitationType = document.getElementById(ids.groupDetailsInvitationType).value;

        const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_CHANGE_INVITATION_TYPE", {groupId: currentGroup.groupId}), {value: invitationType});
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(group){
                groupsController.addGroup(group);
                notificationService.showSuccess(Localization.getAdditionalContent("group-invitation-type-changed"));
            }
        dao.sendRequestAsync(request);
    }

    function kickMember(groupMember){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("kick-group-member-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("kick-group-member-confirmation-dialog-detail", {username: groupMember.username, groupName: currentGroup.name}))
            .withConfirmButton(Localization.getAdditionalContent("kick-group-member-confirmation-dialog-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("kick-group-member-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "kick-group-member-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_DELETE_MEMBER", {groupId: currentGroup.groupId, groupMemberId: groupMember.groupMemberId}));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(Localization.getAdditionalContent("group-member-kicked"));
                        syncEngine.remove(groupMember.groupMemberId);
                    }
                dao.sendRequestAsync(request);
            }
        )
    }

    function addEventListeners(){
        document.getElementById(ids.groupDetailsTitle).addEventListener("focusout", updateName);
        document.getElementById(ids.groupDetailsInvitationType).onchange = changeInvitationType;
    }
})();