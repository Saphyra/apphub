(function RoleController(){
    events.SEARCH_USER_ATTEMPT = "search_user_attempt"

    const roleLocalization = new CustomLocalization("admin_panel", "roles");
    const availableRoles = [];
    let searchUserTimeout = null;
    let previousSearchText = "";

    pageLoader.addLoader(initAvailableRoles, "Initializing available roles");
    pageLoader.addLoader(addEventListeners, "Adding EventListeners");

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.LOCALIZATION_LOADED},
        queryUsers,
        true
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.SEARCH_USER_ATTEMPT},
        function(){
            if(searchUserTimeout){
                clearTimeout(searchUserTimeout);
            }

            searchUserTimeout = setTimeout(queryUsers, getValidationTimeout());
        }
    ));

    function queryUsers(){
        const searchText = document.getElementById("search-input").value;
        if(searchText == previousSearchText){
            return;
        }

        const container = document.getElementById("users");
            container.innerHTML = "";

        if(searchText.length < 3){
            displayTooShortSearchText(container);
            previousSearchText = "";
            return;
        }

        const request = new Request(Mapping.getEndpoint("USER_DATA_GET_USER_ROLES"), {value: searchText});
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(users){
                displayUsers(container, users);
                previousSearchText = searchText;
            }
        dao.sendRequestAsync(request);

        function displayTooShortSearchText(container){
            const node = document.createElement("div");
                node.id = "too-short-search-text";
                node.innerHTML = Localization.getAdditionalContent("too-short-search-text");
            container.appendChild(node);
        }

        function displayUsers(container, users){
            const table = document.createElement("table");
                table.id = "users-table";
                table.classList.add("formatted-table");

                const thead = document.createElement("thead");

                    const headerRow = document.createElement("tr");
                        headerRow.appendChild(createTableHead("username"));
                        headerRow.appendChild(createTableHead("email"));
                        headerRow.appendChild(createTableHead("actual-roles"));
                        headerRow.appendChild(createTableHead("available-roles"));
                thead.appendChild(headerRow);
            table.appendChild(headerRow);

                const tbody = document.createElement("tbody");

                if(!users.length){
                    const row = document.createElement("tr");
                        const cell = document.createElement("td");
                            cell.colSpan = 4;
                            cell.innerHTML = Localization.getAdditionalContent("no-users-found");
                            cell.classList.add("no-users");
                    row.appendChild(cell);
                    tbody.appendChild(row);
                }

                new Stream(users)
                    .map(createUserNode)
                    .forEach(function(node){tbody.appendChild(node)});
            table.appendChild(tbody);
            container.appendChild(table);

            function createTableHead(localizationKey){
                const head = document.createElement("th");
                    head.innerHTML = Localization.getAdditionalContent(localizationKey);
                return head;
            }

            function createUserNode(user){
                const row = document.createElement("tr");

                    const usernameCell = document.createElement("td");
                        usernameCell.innerText = user.username;
                        usernameCell.classList.add("id-cell");
                row.appendChild(usernameCell);

                    const emailCell = document.createElement("td");
                        emailCell.innerText = user.email;
                        emailCell.classList.add("id-cell");
                row.appendChild(emailCell);

                    const actualRolesCell = document.createElement("td");
                        actualRolesCell.classList.add("role-cell");
                row.appendChild(actualRolesCell);

                    const availableRolesCell = document.createElement("td");
                        availableRolesCell.classList.add("role-cell");
                row.appendChild(availableRolesCell);

                setupRoles(user, actualRolesCell, availableRolesCell);

                return row;

                function setupRoles(user, actualRolesCell, availableRolesCell){
                    new Stream(user.roles)
                        .map(function(role){
                            const roleNode = document.createElement("button");
                                roleNode.classList.add("role");
                                roleNode.id = role;
                                roleNode.innerHTML = roleLocalization.get(role);
                                roleNode.onclick = function(){
                                    removeRole(roleNode, role, user, actualRolesCell, availableRolesCell);
                                }
                            return roleNode;
                        })
                        .forEach(function(node){actualRolesCell.appendChild(node)});

                    new Stream(availableRoles)
                        .filter(function(role){return user.roles.indexOf(role) < 0})
                        .map(function(role){
                            const roleNode = document.createElement("button");
                                roleNode.classList.add("role");
                                roleNode.id = role;
                                roleNode.innerHTML = roleLocalization.get(role);
                                roleNode.onclick = function(){
                                    addRole(roleNode, role, user, actualRolesCell, availableRolesCell);
                                }
                            return roleNode;
                        })
                        .forEach(function(node){availableRolesCell.appendChild(node)});
                }
            }
        }
    }

    function removeRole(roleNode, role, user, actualRolesCell, availableRolesCell){
        const payload = {
            userId: user.userId,
            role: role
        };

        const request = new Request(Mapping.getEndpoint("USER_DATA_REMOVE_ROLE"), payload);
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("role-removed"));
                actualRolesCell.removeChild(roleNode);
                availableRolesCell.appendChild(roleNode);
                roleNode.onclick = function(){
                    addRole(roleNode, role, user, actualRolesCell, availableRolesCell);
                }
            };
        dao.sendRequestAsync(request);
    }

    function addRole(roleNode, role, user, actualRolesCell, availableRolesCell){
        const payload = {
            userId: user.userId,
            role: role
        };

        const request = new Request(Mapping.getEndpoint("USER_DATA_ADD_ROLE"), payload);
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("role-added"));
                availableRolesCell.removeChild(roleNode);
                actualRolesCell.appendChild(roleNode);
                roleNode.onclick = function(){
                    removeRole(roleNode, role, user, actualRolesCell, availableRolesCell);
                }
            };
        dao.sendRequestAsync(request);
    }

    function initAvailableRoles(){
        const request = new Request(Mapping.getEndpoint("ADMIN_PANEL_AVAILABLE_ROLES"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(roles){
                new Stream(roles)
                    .forEach(function(role){availableRoles.push(role)});
            }
        dao.sendRequestAsync(request);
    }

    function addEventListeners(){
        $("#search-input").on("keyup", function(){
            eventProcessor.processEvent(new Event(events.SEARCH_USER_ATTEMPT));
        });
    }
})();