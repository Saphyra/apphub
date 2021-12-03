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
                        const date = new Date(0);
                            date.setUTCSeconds(ban.expiration);
                        expirationCell.innerText = formatDate(date);
                row.appendChild(expirationCell);

                    const permanentCell = document.createElement("TD");
                        permanentCell.innerText = Localization.getAdditionalContent(ban.permanent);
                row.appendChild(permanentCell);

                    const reasonCell = document.createElement("TD");
                        const reasonArea = document.createElement("TEXTAREA");
                            reasonArea.value = ban.reason;
                        reasonCell.appendChild(reasonArea);
                row.appendChild(bannedRoleCell);


                    const bannedByCell = document.createElement("TD");
                        bannedByCell.innerText = [ban.bannedById, ban.bannedByUsername, ban.bannedByEmail]
                            .join(" ");
                row.appendChild(bannedByCell);
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