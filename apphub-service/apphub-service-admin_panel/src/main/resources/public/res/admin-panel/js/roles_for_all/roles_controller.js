(function RolesController(){
    const roleLocalization = new CustomLocalization("admin_panel", "roles");

    pageLoader.addLoader(initRoles, "Initializing roles");

    window.rolesController = new function(){

    }

    function initRoles(){
        const restrictedRoleRequests = new Request(Mapping.getEndpoint("USER_DATA_ROLES_FOR_ALL_RESTRICTED"));
            restrictedRoleRequests.convertResponse = jsonConverter;
            restrictedRoleRequests.processValidResponse = function(restrictedRoles){
                const availableRoleRequest = new Request(Mapping.getEndpoint("ADMIN_PANEL_AVAILABLE_ROLES"));
                    availableRoleRequest.convertResponse = jsonConverter
                    availableRoleRequest.processValidResponse = function(roles){
                        const container = document.getElementById(ids.roles);

                        new Stream(roles)
                            .filter(function(role){return restrictedRoles.indexOf(role) < 0})
                            .sorted(function(a, b){return roleLocalization.get(a).localeCompare(roleLocalization.get(b))})
                            .map(createNode)
                            .forEach(function(node){container.appendChild(node)});
                    }
                dao.sendRequestAsync(availableRoleRequest);
            }
        dao.sendRequestAsync(restrictedRoleRequests);
    }

    function createNode(role){
        const row = document.createElement("TR");
            const roleNameCell = document.createElement("TD");
                roleNameCell.innerText = roleLocalization.get(role);
        row.appendChild(roleNameCell);

            const addToAllCell = document.createElement("TD");
                const addToAllButton = document.createElement("BUTTON");
                    addToAllButton.innerText = Localization.getAdditionalContent("add-role-to-all");
                    addToAllButton.onclick = function(){addToAll(role)};
            addToAllCell.appendChild(addToAllButton);
        row.appendChild(addToAllCell);

            const removeFromAllCell = document.createElement("TD");
                const removeFromAllButton = document.createElement("BUTTON");
                    removeFromAllButton.innerText = Localization.getAdditionalContent("remove-role-from-all");
                    removeFromAllButton.onclick = function(){removeFromAll(role)};
            removeFromAllCell.appendChild(removeFromAllButton);
        row.appendChild(removeFromAllCell);
        return row;
    }

    function addToAll(role){
        const passwordInput = document.createElement("INPUT");
            passwordInput.type = "password";
            passwordInput.placeholder = Localization.getAdditionalContent("password");

        const localization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("confirm-add-to-all-title"))
            .withDetail(createDetail(passwordInput, role))
            .withConfirmButton(Localization.getAdditionalContent("add-to-all-button"))
            .withDeclineButton(Localization.getAdditionalContent("cancel"));

        confirmationService.openDialog("confirm-add-to-all", localization, function(){sendAddToAllRequest(role, passwordInput.value)});

        function createDetail(passwordInput, role){
            const container = document.createElement("DIV");

                const detail = document.createElement("DIV");
                    detail.innerHTML = Localization.getAdditionalContent("confirm-add-to-all-detail", {role: roleLocalization.get(role)});
            container.appendChild(detail);

                const passwordContainer = document.createElement("DIV");
                    passwordContainer.id = "add-to-all-password-container";
                    passwordContainer.appendChild(passwordInput);
            container.appendChild(passwordContainer);
            return container;
        }

        function sendAddToAllRequest(role, password){
            if(password.length == 0){
                notificationService.showError(Localization.getAdditionalContent("empty-password"));
                return;
            }

            const request = new Request(Mapping.getEndpoint("USER_DATA_ADD_ROLE_TO_ALL", {role: role}), {value: password});
                request.processValidResponse = function(){
                    notificationService.showSuccess(Localization.getAdditionalContent("role-added-to-all"));
                }
            dao.sendRequestAsync(request);
        }
    }

    function removeFromAll(role){
        const passwordInput = document.createElement("INPUT");
            passwordInput.type = "password";
            passwordInput.placeholder = Localization.getAdditionalContent("password");

        const localization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("confirm-remove-from-all-title"))
            .withDetail(createDetail(passwordInput, role))
            .withConfirmButton(Localization.getAdditionalContent("remove-from-all-button"))
            .withDeclineButton(Localization.getAdditionalContent("cancel"));

        confirmationService.openDialog("confirm-remove-from-all", localization, function(){sendAddToAllRequest(role, passwordInput.value)});

        function createDetail(passwordInput, role){
            const container = document.createElement("DIV");

                const detail = document.createElement("DIV");
                    detail.innerHTML = Localization.getAdditionalContent("confirm-remove-from-all-detail", {role: roleLocalization.get(role)});
            container.appendChild(detail);

                const passwordContainer = document.createElement("DIV");
                    passwordContainer.id = "remove-from-all-password-container";
                    passwordContainer.appendChild(passwordInput);
            container.appendChild(passwordContainer);
            return container;
        }

        function sendAddToAllRequest(role, password){
            if(password.length == 0){
                notificationService.showError(Localization.getAdditionalContent("empty-password"));
                return;
            }

            const request = new Request(Mapping.getEndpoint("USER_DATA_REMOVE_ROLE_FROM_ALL", {role: role}), {value: password});
                request.processValidResponse = function(){
                    notificationService.showSuccess(Localization.getAdditionalContent("role-removed-from-all"));
                }
            dao.sendRequestAsync(request);
        }
    }
})();