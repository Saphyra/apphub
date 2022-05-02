scriptLoader.loadScript("/res/common/js/sync_engine.js");

(function GroupDetailsController(){
    pageLoader.addLoader(addEventListeners, "Add eventListeners to Group Details window");

    let currentGroup;

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
                    let canInvite = false;
                    if(currentGroup.ownerId == window.userId){
                        canInvite = true;
                    } else if(userCanInvite(groupMembers)){
                        canInvite = true;
                    }

                    document.getElementById(ids.groupDetailsAddMemberButton).disabled = !canInvite;

                    syncEngine.addAll(groupMembers);
                }
            dao.sendRequestAsync(request);

            function userCanInvite(groupMembers){
                return new Stream(groupMembers)
                   .filter((groupMember) => {return groupMember.userId = window.userId})
                   .map((groupMember) => {return groupMember.canInvite})
                   .findFirst()
                   .orElseThrow("IllegalState", window.userId + " is not member of group " + groupId);
            }
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
                    canInviteInput.disabled = window.userId != currentGroup.ownerId || groupMember.userId == currentGroup.ownerId;
            canInviteCell.appendChild(canInviteInput);
        node.appendChild(canInviteCell);

            const canKickCell = document.createElement("TD");
                const canKickInput = document.createElement("INPUT");
                    canKickInput.type = "checkbox";
                    canKickInput.checked = groupMember.canKick;
                    canKickInput.disabled = window.userId != currentGroup.ownerId || groupMember.userId == currentGroup.ownerId;
            canKickCell.appendChild(canKickInput);
        node.appendChild(canKickCell);

            const canModifyRolesCell = document.createElement("TD");
                const canModifyRolesInput = document.createElement("INPUT");
                    canModifyRolesInput.type = "checkbox";
                    canModifyRolesInput.checked = groupMember.canModifyRoles;
                    canModifyRolesInput.disabled = window.userId != currentGroup.ownerId || groupMember.userId == currentGroup.ownerId;
            canModifyRolesCell.appendChild(canModifyRolesInput);
        node.appendChild(canModifyRolesCell);

            const kickCell = document.createElement("TD");
                const kickButton = document.createElement("BUTTON");
                    kickButton.innerText = "X";
                    kickButton.disabled = window.userId != currentGroup.ownerId || groupMember.userId == currentGroup.ownerId;
            kickCell.appendChild(kickButton);
        node.appendChild(kickCell);


        canInviteInput.onchange = updateGroupMember(groupMember, canInviteInput, canKickInput, canModifyRolesInput);
        canKickInput.onchange = updateGroupMember(groupMember, canInviteInput, canKickInput, canModifyRolesInput);
        canModifyRolesCell.onchange = updateGroupMember(groupMember, canInviteInput, canKickInput, canModifyRolesInput);

        kickButton.onclick = kickMember(groupMember.groupMemberId)

        return node;
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
        //TODO
    }

    function kickMember(){
        //TODO
    }

    function addEventListeners(){
        document.getElementById(ids.groupDetailsTitle).addEventListener("focusout", updateName);
        document.getElementById(ids.groupDetailsInvitationType).onchange = changeInvitationType;
    }
})();