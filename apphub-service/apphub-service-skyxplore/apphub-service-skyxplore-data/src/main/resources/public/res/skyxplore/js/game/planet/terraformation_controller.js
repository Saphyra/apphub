(function TerraformationController(){
    window.terraformationController = new function(){
        this.openTerraformWindow = openTerraformWindow;
        this.cancelTerraformation = cancelTerraformation;
    }

    function openTerraformWindow(planetId, surfaceId, surfaceType){
        const container = document.getElementById(ids.terraformingPossibilities);
            container.innerHTML = "";

        new Stream(dataCaches.terraformingPossibilities.get(surfaceType))
            .sorted(function(a, b){return dataCaches.surfaceTypeLocalization.get(a.surfaceType).localeCompare(dataCaches.surfaceTypeLocalization.get(b.surfaceType))})
            .map(function(terraformingPossibility){return createNode(planetId, surfaceId, terraformingPossibility)})
            .forEach(function(node){container.appendChild(node)});

        document.getElementById(ids.closeTerraformationButton).onclick = function(){
            planetController.openPlanetWindow();
        };
        switchTab("main-tab", ids.terraformation);

        function createNode(planetId, surfaceId, terraformingPossibility){
            const container = document.createElement("DIV");
                container.classList.add("terraforming-possibility");
                container.id = terraformingPossibility.surfaceType;

                const title = document.createElement("DIV");
                    title.classList.add("terraforming-possibility-title");
                    title.innerText = dataCaches.surfaceTypeLocalization.get(terraformingPossibility.surfaceType);
            container.appendChild(title);

            container.appendChild(createConstructionRequirements(terraformingPossibility.constructionRequirements));

                const terraformButton = document.createElement("BUTTON");
                    terraformButton.classList.add("terraform-button");
                    terraformButton.innerText = Localization.getAdditionalContent("start-terraformation");
                    terraformButton.onclick = function(){
                        startTerraformation(planetId, surfaceId, terraformingPossibility.surfaceType);
                    }
            container.appendChild(terraformButton);

            return container;

            function createConstructionRequirements(constructionRequirements){
                const container = document.createElement("TABLE");
                    container.classList.add("terraforming-possibility-construction-requirements");
                    container.classList.add("formatted-table");

                    const thead = document.createElement("THEAD");
                        const headRow = document.createElement("TR");
                            const headCell = document.createElement("TD");
                                headCell.colSpan = 2;
                                headCell.innerText = Localization.getAdditionalContent("construction-requirements");
                        headRow.appendChild(headCell);
                    thead.appendChild(headRow);
                container.appendChild(thead);

                    const tbody = document.createElement("TBODY");
                        new MapStream(constructionRequirements.requiredResources)
                            .sorted(function(a, b){return dataCaches.itemDataNames.get(a.getKey()).localeCompare(dataCaches.itemDataNames.get(b.getKey()))})
                            .toListStream((resourceDataId, amount) => {return createRow(dataCaches.itemDataNames.get(resourceDataId), amount)})
                            .forEach(function(node){tbody.appendChild(node)});

                    tbody.appendChild(createRow(Localization.getAdditionalContent("parallel-workers"), constructionRequirements.parallelWorkers));
                    tbody.appendChild(createRow(Localization.getAdditionalContent("required-work-points"), constructionRequirements.requiredWorkPoints));
                container.appendChild(tbody);

                return container;

                function createRow(name, amount){
                    const row = document.createElement("TR");
                        const resourceCell = document.createElement("TD");
                            resourceCell.innerText = name;
                    row.appendChild(resourceCell);

                        const amountCell = document.createElement("TD");
                            amountCell.innerText = amount;
                    row.appendChild(amountCell);
                    return row;
                }
            }
        }
    }

    function startTerraformation(planetId, surfaceId, surfaceType){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GAME_TERRAFORM_SURFACE", {planetId: planetId, surfaceId: surfaceId}), {value: surfaceType});
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(surface){
                surfaceViewController.updateSurface(surface);
                planetController.openPlanetWindow();
            }
        dao.sendRequestAsync(request);
    }

    function cancelTerraformation(planetId, surfaceId){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("cancel-terraformation-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("cancel-terraformation-confirmation-dialog-detail"))
            .withConfirmButton(Localization.getAdditionalContent("cancel-terraformation-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("cancel-terraformation-cancel-button"));

        return new Promise((resolve, reject)=>{
            confirmationService.openDialog(
                "cancel-terraformation-confirmation-dialog",
                confirmationDialogLocalization,
                function(){
                    const request = new Request(Mapping.getEndpoint("SKYXPLORE_GAME_CANCEL_TERRAFORMATION", {planetId: planetId, surfaceId: surfaceId}));
                        request.convertResponse = jsonConverter;
                        request.processValidResponse = function(surface){
                            resolve(surface);
                        }
                    dao.sendRequestAsync(request);
                }
            );
        })
    }
})();