(function PopulationOverviewController(){
    const orderTypes = {
        NAME: "name",
        SKILL_LEVEL: "skill_level"
    }

    let population;
    let orderType = orderTypes.NAME;

    pageLoader.addLoader(setUpEventListeners, "PopulationOverview set up event listeners");
    pageLoader.addLoader(setUpPopulationFilters, "PopulationOverview set up population filters");

    window.populationOverviewController = new function(){
        this.viewPopulationOverview = viewPopulationOverview;
        this.setOrderType = function(o){
            orderType = o;
            displayPopulation();
        }
    }

    function viewPopulationOverview(planetId){
        document.getElementById(ids.closePopulationOverviewButton).onclick = function(){
            planetController.viewPlanet(planetId);
        }

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_GET_POPULATION", {planetId: planetId}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(citizens){
                population = citizens;
                displayPopulation();
            };
        dao.sendRequestAsync(request);

        reset();
        switchTab("main-tab", ids.populationOverview);
    }

    function reset(){
        $("#" + ids.populationOverviewSkillSelectionContainer).hide();
        $("#" + ids.populationOverviewOrderContainer).hide();
    }

    function displayPopulation(){
        const populationContainer = document.getElementById(ids.populationOverviewCitizenList);
            populationContainer.innerHTML = "";

            new Stream(population)
                .sorted(sortCitizens)
                .map(createCitizenNode)
                .forEach(function(node){populationContainer.appendChild(node)});

        function sortCitizens(a, b){
            const orderValue = Number(document.querySelector("input[name=population-overview-order-type]:checked").value);

            switch(orderType){
                case orderTypes.NAME:
                    return orderValue * a.name.localeCompare(b.name);
                break;
                case orderTypes.SKILL_LEVEL:
                    const skillType = document.getElementById(ids.populationOverviewOrderSkillListInput).value;

                    return orderValue * (a.skills[skillType].level - b.skills[skillType].level);
                break;
                default:
                    logService.warn("Unknown orderType: " + orderType);
                    return 0;
            }
        }

        function createCitizenNode(citizen){
            const node = document.createElement("DIV");
                node.classList.add("population-overview-citizen");

                const citizenName = document.createElement("DIV");
                    citizenName.classList.add("population-overview-citizen-name");
                    citizenName.innerText = citizen.name;

                    citizenName.onclick = function(){
                        citizenName.contentEditable = true;
                        citizenName.focus();
                    }

                    $(citizenName).on("focusin", function(){selectElementText(citizenName);});
                    $(citizenName).on("focusout", function(){
                        const newName = citizenName.innerText;
                        citizenName.contentEditable = false;
                        clearSelection();
                        citizenName.innerText = citizen.name;
                        if(newName != citizen.name){
                            updateCitizenName(citizen.citizenId, newName);
                        }
                    });
            node.appendChild(citizenName);

                const baseStatContainer = document.createElement("DIV");
                    baseStatContainer.appendChild(createProgressBar(citizen.morale, Localization.getAdditionalContent("morale") + ": " + citizen.morale));
                    baseStatContainer.appendChild(createProgressBar(citizen.satiety, Localization.getAdditionalContent("satiety") + ": " + citizen.satiety));
            node.appendChild(baseStatContainer);

                const displayableSkills = [];
                $(".population-overview-skill-type:checked").each(function(){
                    displayableSkills.push($(this).attr("value"));
                });

                const skillContainer = document.createElement("DIV");
                    skillContainer.classList.add("population-overview-citizen-skills");
                    new MapStream(citizen.skills)
                        .filter(function(skill){return canDisplaySkill(displayableSkills, skill)})
                        .sorted(sortSkills)
                        .map(createSkillProgressBar)
                        .toListStream()
                        .forEach(function(skillNode){skillContainer.appendChild(skillNode)});
            node.appendChild(skillContainer);

            return node;

            function createProgressBar(percentage, text){
                const container = document.createElement("DIV");
                    container.classList.add("progress-bar-container");
                    container.classList.add("population-overview-citizen-progress-bar");

                    const background = document.createElement("DIV");
                        background.classList.add("progress-bar-background");
                        background.style.width = percentage + "%";
                container.appendChild(background);

                    const label = document.createElement("DIV");
                        label.classList.add("progress-bar-text");
                        label.innerHTML = text;
                container.appendChild(label);

                return container;
            }

            function canDisplaySkill(displayableSkills, skillType){
                return displayableSkills.indexOf(skillType) > -1;
            }

            function sortSkills(a, b){
                return skillTypeLocalization.get(a.getKey()).localeCompare(skillTypeLocalization.get(b.getKey()));
            }

            function createSkillProgressBar(skillType, skill){
                const percentage = skill.experience / skill.nextLevel * 100;
                const label = skillTypeLocalization.get(skillType) + " - lvl " + skill.level + " (" + Math.floor(percentage) + "%)";
                return createProgressBar(percentage, label);
            }
        }
    }

    function updateCitizenName(citizenId, newName){
        if(newName.length < 3){
            notificationService.showError(Localization.getAdditionalContent("citizen-name-too-short"));
            return;
        }
        if(newName.length > 30){
            notificationService.showError(Localization.getAdditionalContent("citizen-name-too-long"));
            return;
        }
        const planetId = planetController.getOpenedPlanetId();
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_RENAME_CITIZEN", {planetId: planetId, citizenId: citizenId}), {value: newName});
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("citizen-renamed"));
                viewPopulationOverview(planetId);
            }
        dao.sendRequestAsync(request);
    }

    function setUpEventListeners(){
        document.getElementById(ids.populationOverviewSkillSelectionToggleButton).onclick = function(){
            $("#" + ids.populationOverviewSkillSelectionContainer).toggle();
        }

        document.getElementById(ids.populationOverviewOrderToggleButton).onclick = function(){
            $("#" + ids.populationOverviewOrderContainer).toggle();
        }

        document.getElementById(ids.populationOverviewShowAllSkills).onclick = function(){
            $(".population-overview-skill-type").prop("checked", true);
            displayPopulation();
        }

        document.getElementById(ids.populationOverviewHideAllSkills).onclick = function(){
            $(".population-overview-skill-type").prop("checked", false);
            displayPopulation();
        }
    }
    function setUpPopulationFilters(){
        const skillTypeCheckboxes = document.getElementById(ids.populationOverviewSkillList);
        new Stream(skillTypeLocalization.getKeys())
            .sorted(function(a, b){return skillTypeLocalization.get(a).localeCompare(skillTypeLocalization.get(b))})
            .map(createSkillTypeCheckbox)
            .forEach(function(node){skillTypeCheckboxes.appendChild(node)});

        const skillTypeSelectInput = document.getElementById(ids.populationOverviewOrderSkillListInput);
            skillTypeSelectInput.onchange = displayPopulation;
        new Stream(skillTypeLocalization.getKeys())
            .sorted(function(a, b){return skillTypeLocalization.get(a).localeCompare(skillTypeLocalization.get(b))})
            .map(createSkillTypeOption)
            .forEach(function(node){skillTypeSelectInput.appendChild(node)});

        $(".population-overview-order-type").on("change", displayPopulation);

        function createSkillTypeCheckbox(skillType){
            const node = document.createElement("LABEL");
                const checkbox = document.createElement("INPUT");
                    checkbox.type = "checkbox";
                    checkbox.value = skillType;
                    checkbox.classList.add("population-overview-skill-type");
                    checkbox.checked = true;
                    checkbox.onchange = displayPopulation;
                node.appendChild(checkbox);

                const label = document.createElement("SPAN");
                    label.innerHTML = skillTypeLocalization.get(skillType);
            node.appendChild(label);
            return node;
        }

        function createSkillTypeOption(skillType){
            const option = document.createElement("OPTION");
                option.value = skillType;
                option.innerHTML = skillTypeLocalization.get(skillType);
            return option;
        }
    }
})();