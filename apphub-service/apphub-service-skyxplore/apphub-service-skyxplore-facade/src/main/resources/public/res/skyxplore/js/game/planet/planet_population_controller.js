(function PlanetPopulationController(){
    window.planetPopulationController = new function(){
        this.loadPopulation = loadPopulation;
    }

    $(document).ready(init)

    function loadPopulation(planetId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_GET_POPULATION_OVERVIEW", {planetId: planetId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(population){
                displayPopulation(population);
            }
        dao.sendRequestAsync(request);
    }

    function displayPopulation(population){
        const width = population.population / population.capacity * 100;

        const barBackground = document.getElementById(ids.planetPopulationOverviewProgressBarBackground);
            barBackground.style.width = width + "%";
            if(width == 100){
                barBackground.style.background = "red";
            }else if(width >= 80){
                barBackground.style.background = "orange";
            }else{
                barBackground.style.background = "green";
            }

        document.getElementById(ids.planetPopulationOverviewActual).innerHTML = population.population;
        document.getElementById(ids.planetPopulationOverviewCapacity).innerHTML = population.capacity;
    }

    function init(){
        document.getElementById(ids.planetOpenPopulationOverviewButton).onclick = function(){
            //TODO
        }
    }
})();