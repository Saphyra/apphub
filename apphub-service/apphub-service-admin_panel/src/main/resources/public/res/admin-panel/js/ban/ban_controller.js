(function BanController(){
    const roleLocalization = new CustomLocalization("admin_panel", "roles");
    const availableRoles = [];

    let openedUserId = null;

    pageLoader.addLoader(initAvailableRoles, "Initializing available roles");

    window.banController = new function(){
        this.openUser = openUser;
        this.createBan = createBan;
        this.close = function(){
            switchTab("main-page", ids.userList);
        }
    }

    function openUser(userId){
        const request = new Request(Mapping.getEndpoint("ACCOUNT_GET_BANS", {userId: userId}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(response){
                setUserData(response);
                displayCurrentBans(response.userId, response.bans);
                setBannableRoles(getBannedRoles(response.bans));
                resetInputFields();

                openedUserId = userId;
                switchTab("main-page", ids.userDetails);
            }
        dao.sendRequestAsync(request);

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
                    selectOption.innerText = Localization.getAdditionalContent("select-role");
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
                        bannedRoleCell.innerText = ban.bannedRole;
                row.appendChild(bannedRoleCell);

                    const expirationCell = document.createElement("TD");
                        if(ban.expiration != null){
                            const date = new Date(0);
                                date.setUTCSeconds(ban.expiration);
                            expirationCell.innerText = formatDate(date);
                        }
                row.appendChild(expirationCell);

                    const permanentCell = document.createElement("TD");
                        permanentCell.innerText = Localization.getAdditionalContent(ban.permanent);
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
                            revokeButton.innerHTML = Localization.getAdditionalContent("revoke-button");
                            revokeButton.onclick = function(){
                                revokeBan(ban.id);
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
            notificationService.showError(Localization.getAdditionalContent("select-banned-role"));
            return;
        }

        const permanent = document.getElementById(ids.permanent).checked;
        let duration = null;
        let chronoUnit = null;
        if(!permanent){
            duration = document.getElementById(ids.duration).value;
            if(duration <= 0){
                notificationService.showError(Localization.getAdditionalContent("duration-too-low"));
                return;
            }

            chronoUnit = document.getElementById(chronoUnit).value;
            if(chronoUnit.length == 0){
                notificationService.showError(Localization.getAdditionalContent("select-chrono-unit"));
                return;
            }
        }

        const reason = document.getElementById(ids.reason).value;
        if(isBlank(reason)){
            notificationService.showError(Localization.getAdditionalContent("blank-reason"));
            return;
        }

        const password = document.getElementById(ids.password).value;
        if(password.length == 0){
            notificationService.showError(Localization.getAdditionalContent("blank-password"));
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
                notificationService.showSuccess(Localization.getAdditionalContent("user-banned"));
                openUser(openedUserId);
            }
        dao.sendRequestAsync(request);
    }

    function revokeBan(banId){
        console.log(banId);
        //TODO implement
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
})();