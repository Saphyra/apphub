(function PopulationOverviewController(){
    let population;

    window.populationOverviewController = new function(){
        this.viewPopulationOverview = viewPopulationOverview;
    }

    $(document).ready(init);

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
            return 0; //TODO
        }

        function createCitizenNode(citizen){
            const node = document.createElement("DIV");
                node.classList.add("population-overview-citizen");

                const citizenName = document.createElement("DIV");
                    citizenName.classList.add("population-overview-citizen-name");
                    citizenName.innerHTML = citizen.name;
            node.appendChild(citizenName);

                const baseStatContainer = document.createElement("DIV");
                    baseStatContainer.appendChild(createProgressBar(citizen.morale, Localization.getAdditionalContent("morale") + ": " + citizen.morale));
                    baseStatContainer.appendChild(createProgressBar(citizen.satiety, Localization.getAdditionalContent("satiety") + ": " + citizen.satiety));
            node.appendChild(baseStatContainer);

                const skillContainer = document.createElement("DIV");
                    skillContainer.classList.add("population-overview-citizen-skills");
                    new MapStream(citizen.skills)
                        .filter(canDisplaySkill)
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

            function canDisplaySkill(skillType){
                return true; //TODO
            }

            function sortSkills(a, b){
                return 0; //TODO
            }

            function createSkillProgressBar(skillType, skill){
                const percentage = skill.experience / skill.nextLevel * 100;
                const label = skillTypeLocalization.get(skillType) + " - lvl " + skill.level + " (" + Math.floor(percentage) + "%)";
                return createProgressBar(percentage, label);
            }
        }
    }

    function init(){
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