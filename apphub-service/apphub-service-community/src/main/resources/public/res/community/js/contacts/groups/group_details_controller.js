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
        this.disbandGroup = disbandGroup;
        this.leaveGroup = leaveGroup;
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

                    const buttonId = currentGroup.ownerId == window.userId ? ids.disbandGroupButton : ids.leaveGroupButton;
                    switchTab("group-details-button", buttonId);

                    document.getElementById(ids.groupDetailsTransferOwnership).style.display = window.userId == currentGroup.ownerId ? "table-cell" : "none";

                    let canInvite = currentGroup.ownerId == window.userId || ownMember.canInvite;

                    document.getElementById(ids.groupDetailsAddMemberButton).disabled = !canInvite;

                    syncEngine.addAll(groupMembers);
                }
            dao.sendRequestAsync(request);
        }
    }

    function createGroupMemberNode(groupMember){
        const node = document.createElement("TR");
            node.title = groupMember.email;

            const usernameCell = document.createElement("TD");
                usernameCell.innerText = groupMember.username;
        node.appendChild(usernameCell);

            const canInviteCell = document.createElement("TD");
                const canInviteInput = document.createElement("INPUT");
                    canInviteInput.classList.add("can-invite-checkbox")
                    canInviteInput.type = "checkbox";
                    canInviteInput.checked = groupMember.canInvite;
                    canInviteInput.disabled = !canOperate(groupMember, ownMember.canModifyRoles);
            canInviteCell.appendChild(canInviteInput);
        node.appendChild(canInviteCell);

            const canKickCell = document.createElement("TD");
                const canKickInput = document.createElement("INPUT");
                    canKickInput.classList.add("can-kick-checkbox")
                    canKickInput.type = "checkbox";
                    canKickInput.checked = groupMember.canKick;
                    canKickInput.disabled = !canOperate(groupMember, ownMember.canModifyRoles);
            canKickCell.appendChild(canKickInput);
        node.appendChild(canKickCell);

            const canModifyRolesCell = document.createElement("TD");
                const canModifyRolesInput = document.createElement("INPUT");
                    canModifyRolesInput.classList.add("can-modify-roles-checkbox")
                    canModifyRolesInput.type = "checkbox";
                    canModifyRolesInput.checked = groupMember.canModifyRoles;
                    canModifyRolesInput.disabled = !canOperate(groupMember, ownMember.canModifyRoles);
            canModifyRolesCell.appendChild(canModifyRolesInput);
        node.appendChild(canModifyRolesCell);

            const kickCell = document.createElement("TD");
                const kickButton = document.createElement("BUTTON");
                    kickButton.classList.add("kick-button")
                    kickButton.innerText = "X";
                    kickButton.disabled = !canOperate(groupMember, ownMember.canKick);
            kickCell.appendChild(kickButton);
        node.appendChild(kickCell);

            if(window.userId == currentGroup.ownerId){
                const transferLeadershipCell = document.createElement("TD");
                    const transferLeadershipButton = document.createElement("BUTTON");
                        transferLeadershipButton.classList.add("transfer-leadership-button")
                        transferLeadershipButton.innerText = localization.getAdditionalContent("transfer-leadership-button");
                        transferLeadershipButton.onclick = function(){
                            transferLeadership(groupMember);
                        }
                        transferLeadershipButton.disabled = window.userId == groupMember.userId;
                transferLeadershipCell.appendChild(transferLeadershipButton);
                node.appendChild(transferLeadershipCell);
            }


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
            notificationService.showError(localization.getAdditionalContent("group-name-too-short"));
            titleNode.innerText = currentGroup.name;
            return;
        }

        if(name.length > 30){
            notificationService.showError(localization.getAdditionalContent("group-name-too-long"));
            titleNode.innerText = currentGroup.name;
            return;
        }

        const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_RENAME", {groupId: currentGroup.groupId}), {value: name});
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(group){
                groupsController.addGroup(group);
                notificationService.showSuccess(localization.getAdditionalContent("group-renamed"));
            }
        dao.sendRequestAsync(request);
    }

    function updateGroupMember(groupMember, canInviteInput, canKickInput, canModifyRolesInput){
        const payload = {
            canInvite: canInviteInput.checked,
            canKick: canKickInput.checked,
            canModifyRoles: canModifyRolesInput.checked
        };

        const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_MEMBER_ROLES", {groupId: currentGroup.groupId, groupMemberId: groupMember.groupMemberId}), payload);
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(modifiedMember){
                syncEngine.add(modifiedMember);
            }

        if(groupMember.canInvite != canInviteInput.checked){
            if(canInviteInput.checked){
                openConfirmationDialog(
                    groupMember.username,
                    "role-can-invite",
                    () => {dao.sendRequestAsync(request)}
                );
            }else{
                dao.sendRequestAsync(request);
            }
        }else if(groupMember.canKick != canKickInput.checked){
            if(canKickInput.checked){
                openConfirmationDialog(
                    groupMember.username,
                    "role-can-kick",
                    () => {dao.sendRequestAsync(request)}
                );
            }else{
                dao.sendRequestAsync(request);
            }
        }else if(groupMember.canModifyRoles != canModifyRolesInput.checked){
             if(canModifyRolesInput.checked){
                 openConfirmationDialog(
                     groupMember.username,
                     "role-can-modify-roles",
                     () => {dao.sendRequestAsync(request)}
                 );
             }else{
                 dao.sendRequestAsync(request);
             }
        }

        function openConfirmationDialog(username, roleLocalizationKey, callback){
            const confirmationDialogLocalization = new ConfirmationDialogLocalization()
                .withTitle(localization.getAdditionalContent("grant-role-to-member-confirmation-dialog-title"))
                .withDetail(localization.getAdditionalContent("grant-role-to-member-confirmation-dialog-detail", {username: username, groupName: currentGroup.name, role: localization.getAdditionalContent(roleLocalizationKey)}))
                .withConfirmButton(localization.getAdditionalContent("grant-role-to-member-confirmation-dialog-confirm-button"))
                .withDeclineButton(localization.getAdditionalContent("grant-role-to-member-confirmation-dialog-cancel-button"));

            confirmationService.openDialog(
                "grant-role-to-member-confirmation-dialog",
                confirmationDialogLocalization,
                callback,
                ()=>{syncEngine.reload()}
            )
        }
    }

    function changeInvitationType(){
        const invitationType = document.getElementById(ids.groupDetailsInvitationType).value;

        const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_CHANGE_INVITATION_TYPE", {groupId: currentGroup.groupId}), {value: invitationType});
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(group){
                groupsController.addGroup(group);
                notificationService.showSuccess(localization.getAdditionalContent("group-invitation-type-changed"));
            }
        dao.sendRequestAsync(request);
    }

    function kickMember(groupMember){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("kick-group-member-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("kick-group-member-confirmation-dialog-detail", {username: groupMember.username, groupName: currentGroup.name}))
            .withConfirmButton(localization.getAdditionalContent("kick-group-member-confirmation-dialog-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("kick-group-member-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "kick-group-member-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_DELETE_MEMBER", {groupId: currentGroup.groupId, groupMemberId: groupMember.groupMemberId}));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(localization.getAdditionalContent("group-member-kicked"));
                        syncEngine.remove(groupMember.groupMemberId);
                    }
                dao.sendRequestAsync(request);
            }
        )
    }

    function disbandGroup(){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("disband-group-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("disband-group-confirmation-dialog-detail", {groupName: currentGroup.name}))
            .withConfirmButton(localization.getAdditionalContent("disband-group-confirmation-dialog-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("disband-group-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "disband-group-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_DELETE", {groupId: currentGroup.groupId}));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(localization.getAdditionalContent("group-disbanded"));
                        pageController.displayMainPage();
                        groupsController.removeGroup(currentGroup.groupId);
                    }
                dao.sendRequestAsync(request);
            }
        )
    }

    function leaveGroup(){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("leave-group-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("leave-group-confirmation-dialog-detail", {groupName: currentGroup.name}))
            .withConfirmButton(localization.getAdditionalContent("leave-group-confirmation-dialog-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("leave-group-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "leave-group-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_DELETE_MEMBER", {groupId: currentGroup.groupId, groupMemberId: ownMember.groupMemberId}));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(localization.getAdditionalContent("group-left"));
                        pageController.displayMainPage();
                        groupsController.removeGroup(currentGroup.groupId);
                    }
                dao.sendRequestAsync(request);
            }
        )
    }

    function transferLeadership(groupMember){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("transfer-ownership-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("transfer-ownership-confirmation-dialog-detail", {groupName: currentGroup.name, username: groupMember.username}))
            .withConfirmButton(localization.getAdditionalContent("transfer-ownership-confirmation-dialog-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("transfer-ownership-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "transfer-ownership-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_CHANGE_OWNER", {groupId: currentGroup.groupId}), {value: groupMember.groupMemberId});
                    request.processValidResponse = function(){
                        notificationService.showSuccess(localization.getAdditionalContent("ownership-transferred"));
                        pageController.displayMainPage();
                        groupsController.load();
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