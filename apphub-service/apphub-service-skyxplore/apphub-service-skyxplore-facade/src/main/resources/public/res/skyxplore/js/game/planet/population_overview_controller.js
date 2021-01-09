(function PopulationOverviewController(){
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
            request.processValidResponse = function(population){
                displayPopulation(population);
            };
        dao.sendRequestAsync(request);

        reset();
        switchTab("main-tab", ids.populationOverview);
    }

    function reset(){
        $("#" + ids.populationOverviewSkillSelectionContainer).hide();
        $("#" + ids.populationOverviewOrderContainer).hide();
    }

    function displayPopulation(population){
        console.log(population);
    }

    function init(){
        document.getElementById(ids.populationOverviewSkillSelectionToggleButton).onclick = function(){
            $("#" + ids.populationOverviewSkillSelectionContainer).toggle();
        }

        document.getElementById(ids.populationOverviewOrderToggleButton).onclick = function(){
            $("#" + ids.populationOverviewOrderContainer).toggle();
        }
    }
})();