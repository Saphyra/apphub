(function BanController(){
    const roleLocalization = localization.loadCustomLocalization("admin-panel", "roles");
    const availableRoles = [];

    let openedUserId = null;

    pageLoader.addLoader(initAvailableRoles, "Initializing available roles");

    window.banController = new function(){
        this.openUser = openUser;
        this.createBan = createBan;
        this.close = function(){
            switchTab("main-page", ids.userList);
        }
        this.markUserForDeletion = markUserForDeletion;
        this.unmarkUserForDeletion = unmarkUserForDeletion;
    }

    function openUser(userId){
        openedUserId = userId;

        const request = new Request(Mapping.getEndpoint("ACCOUNT_GET_BANS", {userId: userId}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(response){
                displayUser(response);
            }
        dao.sendRequestAsync(request);
    }

    function displayUser(response){
        setUserData(response);
        displayCurrentBans(response.userId, response.bans);
        setBannableRoles(getBannedRoles(response.bans));
        setDeletionStatus(response);
        resetInputFields();
        switchTab("main-page", ids.userDetails);

        function getBannedRoles(bans){
            return new Stream(bans)
                .map(function(ban){return ban.bannedRole})
                .distinct()
                .toList();
        }

        function setBannableRoles(bannedRoles){
            const selectMenu = document.getElementById(ids.bannableRoles);
                selectMenu.innerHTML = "";

                const selectOption = document.createElement("OPTION");
                    selectOption.value = "";
                    selectOption.innerText = localization.getAdditionalContent("select-role");
                    selectOption.selected = true;
                selectMenu.appendChild(selectOption);

            new Stream(availableRoles)
                .filter(function(availableRole){return bannedRoles.indexOf(availableRole) < 0})
                .sorted(function(a, b){return roleLocalization.get(a).localeCompare(roleLocalization.get(b))})
                .map(function(role){
                    const option = document.createElement("OPTION");
                        option.value = role;
                        option.innerText = roleLocalization.get(role);
                    return option;
                }).forEach(function(node){selectMenu.appendChild(node)});
        }

        function displayCurrentBans(userId, bans){
            const container = document.getElementById(ids.currentBans);
                container.innerHTML = "";

                if(bans.length == 0){
                    switchTab("current-bans-result", ids.noCurrentBans);
                    return;
                }

                new Stream(bans)
                    .map(function(ban){return createNode(userId, ban)})
                    .forEach(function(node){container.appendChild(node)});

                switchTab("current-bans-result", ids.currentBans);

            function createNode(userId, ban){
                const row = document.createElement("TR");

                    const banIdCell = document.createElement("TD");
                        banIdCell.innerText = ban.id;
                row.appendChild(banIdCell);

                    const bannedRoleCell = document.createElement("TD");
                        bannedRoleCell.innerText = roleLocalization.get(ban.bannedRole);
                row.appendChild(bannedRoleCell);

                    const expirationCell = document.createElement("TD");
                        expirationCell.classList.add("nowrap");
                        if(ban.expiration != null){
                            const date = new Date(0);
                                date.setUTCSeconds(ban.expiration);
                            expirationCell.innerText = formatDate(date);
                        }
                row.appendChild(expirationCell);

                    const permanentCell = document.createElement("TD");
                        permanentCell.innerText = localization.getAdditionalContent(ban.permanent);
                row.appendChild(permanentCell);

                    const reasonCell = document.createElement("TD");
                        const reasonArea = document.createElement("TEXTAREA");
                            reasonArea.value = ban.reason;
                            reasonArea.disabled = true;
                        reasonCell.appendChild(reasonArea);
                row.appendChild(reasonCell);

                    const bannedByCell = document.createElement("TD");
                        bannedByCell.innerText = [ban.bannedById, ban.bannedByUsername, ban.bannedByEmail]
                            .join(" ");
                row.appendChild(bannedByCell);

                    const revokeCell = document.createElement("TD");
                        const revokeButton = document.createElement("BUTTON");
                            revokeButton.innerHTML = localization.getAdditionalContent("revoke-button");
                            revokeButton.onclick = function(){
                                revokeBan(ban.id, ban.bannedRole);
                            }
                    revokeCell.appendChild(revokeButton);
                row.appendChild(revokeCell);
                return row;
            }
        }

        function setUserData(response){
            document.getElementById(ids.userDetailsUserId).innerText = response.userId;
            document.getElementById(ids.userDetailsUsername).innerText = response.username;
            document.getElementById(ids.userDetailsEmail).innerText = response.email;
        }

        function setDeletionStatus(response){
            const markedForDeletionValue = document.getElementById(ids.markedForDeletion);
            const markedForDeletionAtValue = document.getElementById(ids.markedForDeletionAt);
            const markedForDeletionAtContainer = document.getElementById(ids.markedForDeletionAtContainer);
            const unmarkForDeletionButton = document.getElementById(ids.unmarkForDeletionButton);
            const deleteUserInputContainer = document.getElementById(ids.deleteUserInputContainer);

            if(response.markedForDeletion){
                deleteUserInputContainer.style.display = "none";
                markedForDeletionAtContainer.style.display = "block";
                unmarkForDeletionButton.style.display = "block";

                const date = new Date(0);
                     date.setUTCSeconds(response.markedForDeletionAt);

                markedForDeletionAtValue.innerText = formatDate(date);
            }else{
                deleteUserInputContainer.style.display = "block";
                markedForDeletionAtContainer.style.display = "none";
                unmarkForDeletionButton.style.display = "none";
            }

            markedForDeletionValue.innerText = localization.getAdditionalContent(response.markedForDeletion);
        }

        function resetInputFields(){
            document.getElementById(ids.permanent).checked = false;
            document.getElementById(ids.duration).value = 0;
            document.getElementById(ids.chronoUnit).value = "";
            document.getElementById(ids.reason).value = "";
            document.getElementById(ids.password).value = "";
        }
    }

    function createBan(){
        const bannedRole = document.getElementById(ids.bannableRoles).value;
        if(bannedRole.length == 0){
            notificationService.showError(localization.getAdditionalContent("select-banned-role"));
            return;
        }

        const permanent = document.getElementById(ids.permanent).checked;
        let duration = null;
        let chronoUnit = null;
        if(!permanent){
            duration = document.getElementById(ids.duration).value;
            if(duration <= 0){
                notificationService.showError(localization.getAdditionalContent("duration-too-low"));
                return;
            }

            chronoUnit = document.getElementById(ids.chronoUnit).value;
            if(chronoUnit.length == 0){
                notificationService.showError(localization.getAdditionalContent("select-chrono-unit"));
                return;
            }
        }

        const reason = document.getElementById(ids.reason).value;
        if(isBlank(reason)){
            notificationService.showError(localization.getAdditionalContent("blank-reason"));
            return;
        }

        const password = document.getElementById(ids.password).value;
        if(password.length == 0){
            notificationService.showError(localization.getAdditionalContent("blank-password"));
            return;
        }

        const payload = {
            bannedUserId: openedUserId,
            bannedRole: bannedRole,
            permanent: permanent,
            duration: duration,
            chronoUnit: chronoUnit,
            reason: reason,
            password: password
        };

        const request = new Request(Mapping.getEndpoint("ACCOUNT_BAN_USER"), payload);
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("user-banned"));
                openUser(openedUserId);
            }
        dao.sendRequestAsync(request);
    }

    function revokeBan(banId, bannedRole){
        const passwordInput = document.createElement("INPUT");
            passwordInput.type = "password";
            passwordInput.placeholder = localization.getAdditionalContent("password");

        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("confirm-revoke-ban-confirmation-dialog-title"))
            .withDetail(createDetail(passwordInput, bannedRole))
            .withConfirmButton(localization.getAdditionalContent("revoke-ban"))
            .withDeclineButton(localization.getAdditionalContent("cancel"));

        confirmationService.openDialog("confirm-revoke-ban", confirmationDialogLocalization, function(){sendRevokeBanRequest(banId, passwordInput.value)});

        function createDetail(passwordInput, bannedRole){
            const container = document.createElement("DIV");

                const detail = document.createElement("DIV");
                    detail.innerHTML = localization.getAdditionalContent("confirm-revoke-ban-confirmation-dialog-detail", {bannedRole: roleLocalization.get(bannedRole)});
            container.appendChild(detail);

                const passwordContainer = document.createElement("DIV");
                    passwordContainer.id = "revoke-role-confirm-password-container";
                    passwordContainer.appendChild(passwordInput);
            container.appendChild(passwordContainer);
            return container;
        }

        function sendRevokeBanRequest(banId, password){
            if(password.length == 0){
                notificationService.showError(localization.getAdditionalContent("blank-password"));
                return;
            }

            const request = new Request(Mapping.getEndpoint("ACCOUNT_REMOVE_BAN", {banId: banId}), {value: password});
                request.processValidResponse = function(){
                    notificationService.showSuccess(localization.getAdditionalContent("ban-revoked"));
                    openUser(openedUserId);
                }
            dao.sendRequestAsync(request);
        }
    }

    function initAvailableRoles(){
        const request = new Request(Mapping.getEndpoint("ADMIN_PANEL_AVAILABLE_ROLES"));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(roles){
                new Stream(roles)
                    .forEach(function(role){availableRoles.push(role)});
            }
        dao.sendRequestAsync(request);
    }

    function markUserForDeletion(){
        const date = document.getElementById(ids.deleteTheUserAtDate).value;
        const time = document.getElementById(ids.deleteTheUserAtTime).value;

        if(date.length == 0){
            notificationService.showError(localization.getAdditionalContent("date-empty"));
            return;
        }

        if(time.length == 0){
            notificationService.showError(localization.getAdditionalContent("time-empty"));
            return;
        }

        const passwordInput = document.createElement("INPUT");
            passwordInput.id = "confirm-delete-user-password";
            passwordInput.type = "password";
            passwordInput.placeholder = localization.getAdditionalContent("password");
            passwordInput.classList.add("confirm-password");

        const detail = document.createElement("DIV");
            const text = document.createElement("DIV");
                text.innerHTML = localization.getAdditionalContent("confirm-delete-user-detail");
        detail.appendChild(text);
        detail.appendChild(passwordInput);

        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("confirm-delete-user-title"))
            .withDetail(detail)
            .withConfirmButton(localization.getAdditionalContent("delete-user"))
            .withDeclineButton(localization.getAdditionalContent("cancel"));

        const options = new ConfirmationDialogOptions()
            .withCloseAfterChoice(false);

        const confirmCallback = function(){
            const password = passwordInput.value;
            if(password.length == 0){
                notificationService.showError(localization.getAdditionalContent("empty-password"));
                return;
            }

            const request = new Request(Mapping.getEndpoint("ACCOUNT_MARK_FOR_DELETION", {userId: openedUserId}), {date: date, time: time, password: password});
                request.convertResponse = jsonConverter;
                request.processValidResponse = function(response){
                    confirmationService.closeDialog(ids.confirmationDialogId);
                    displayUser(response);
                }
            dao.sendRequestAsync(request);

            passwordInput.value = "";
        }

        const declineCallback = function(){
            passwordInput.value = "";
            confirmationService.closeDialog(ids.confirmationDialogId);
        }

        confirmationService.openDialog(ids.confirmationDialogId, confirmationDialogLocalization, confirmCallback, declineCallback, options);
    }

    function unmarkUserForDeletion(){
        const request = new Request(Mapping.getEndpoint("ACCOUNT_UNMARK_FOR_DELETION", {userId: openedUserId}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(response){
                displayUser(response);
            }
        dao.sendRequestAsync(request);
    }
})();