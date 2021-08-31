(function DisabledRoleController(){
    const roleLocalization = new CustomLocalization("admin_panel", "roles");

    pageLoader.addLoader(loadRoles, "Load disabled roles");

    function loadRoles(){
        const request = new Request(Mapping.getEndpoint("USER_DATA_GET_DISABLED_ROLES"));
            request.convertResponse = function(response){
                return new Stream(JSON.parse(response.body))
                    .peek(function(role){
                        role.name = roleLocalization.get(role.role);
                    })
                    .sorted(function(a, b){return a.name.localeCompare(b.name)})
                    .toList();
            }
            request.processValidResponse = displayRoles;
        dao.sendRequestAsync(request);
    }

    function displayRoles(roles){
        const container = document.getElementById(ids.roleContainer);
            container.innerHTML = "";

            new Stream(roles)
                .map(createRole)
                .forEach(function(node){container.appendChild(node)});

        function createRole(role){
            const row = document.createElement("TR");
                const nameColumn = document.createElement("TD");
                    nameColumn.classList.add("role-name");
                    nameColumn.innerText = role.name;
            row.appendChild(nameColumn);
                const enabledColumn = document.createElement("TD");
                    enabledColumn.classList.add("role-enabled");
                    const checkbox = document.createElement("INPUT");
                        checkbox.type = "checkbox";
                        checkbox.checked = !role.disabled;
                        checkbox.onchange = function(){
                            role.disabled ? enableRole(role) : disableRole(role)
                        };
                enabledColumn.appendChild(checkbox);
            row.appendChild(enabledColumn);
            return row;
        }
    }

    function enableRole(role){
        const passwordInput = createPasswordInput();

        const localization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("confirm-enable-role"))
            .withDetail(createDetail("enable-role-detail", passwordInput, role.name))
            .withConfirmButton(Localization.getAdditionalContent("enable-role"))
            .withDeclineButton(Localization.getAdditionalContent("cancel"));

        promptAndExecute(role, Mapping.getEndpoint("USER_DATA_ENABLE_ROLE", {role: role.role}), localization, passwordInput, "role-enabled");
    }

    function disableRole(role){
        const passwordInput = createPasswordInput();

        const localization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("confirm-disable-role"))
            .withDetail(createDetail("disable-role-detail", passwordInput, role.name))
            .withConfirmButton(Localization.getAdditionalContent("disable-role"))
            .withDeclineButton(Localization.getAdditionalContent("cancel"));

        promptAndExecute(role, Mapping.getEndpoint("USER_DATA_DISABLE_ROLE", {role: role.role}), localization, passwordInput, "role-disabled");
    }

    function createPasswordInput(){
        const input = document.createElement("INPUT");
            input.type = "password";
            input.placeholder = Localization.getAdditionalContent("password");
            input.classList.add("confirm-password");
        return input;
    }

    function createDetail(textId, passwordInput, roleName){
        const container = document.createElement("DIV");
            const text = document.createElement("DIV");
                text.innerHTML = Localization.getAdditionalContent(textId, {roleName: roleName});
        container.appendChild(text);
        container.appendChild(passwordInput);
        return container;
    }

    function promptAndExecute(role, endpoint, localization, passwordInput, successMessageId){
        const options = new ConfirmationDialogOptions()
            .withCloseAfterChoice(false);

        const confirmCallback = function(){
            const password = passwordInput.value;
            if(password.length == 0){
                notificationService.showError(Localization.getAdditionalContent("empty-password"));
                return;
            }

            const request = new Request(endpoint, {value: password});
                request.processValidResponse = function(){
                    confirmationService.closeDialog(ids.confirmationDialogId);
                    notificationService.showSuccess(Localization.getAdditionalContent(successMessageId));
                    loadRoles();
                }
            dao.sendRequestAsync(request);
            passwordInput.value = "";
        }

        const declineCallback = function(){
            passwordInput.value = "";
            loadRoles();
            confirmationService.closeDialog(ids.confirmationDialogId);
        }

        confirmationService.openDialog(ids.confirmationDialogId, localization, confirmCallback, declineCallback, options);
    }
})();