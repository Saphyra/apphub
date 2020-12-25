(function SolarSystemController(){
    window.solarSystemConstants = {
        SOLAR_SYSTEM_BORDER_WIDTH: 10,
        OFFSET: 70,
        STAR_RADIUS: 40,
        PLANET_RADIUS: 20,
        PLANET_NAME_OFFSET: 30,
    }

    let openedSolarSystemId = null;

    window.solarSystemController = new function(){
        this.viewSolarSystem = viewSolarSystem;
    }

    $(document).ready(init);

    function viewSolarSystem(solarSystemId){
        openedSolarSystemId = solarSystemId;

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_SOLAR_SYSTEM", {solarSystemId: solarSystemId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(solarSystem){
                displaySolarSystem(solarSystem);
            }
        dao.sendRequestAsync(request);
    }

    function displaySolarSystem(solarSystem){
        document.getElementById(ids.solarSystemName).innerText = solarSystem.systemName;

        const radius = solarSystem.radius + solarSystemConstants.OFFSET + solarSystemConstants.SOLAR_SYSTEM_BORDER_WIDTH;

        const svgContainer = document.getElementById(ids.solarSystemSvgContainer);
            svgContainer.innerHTML = "";

            const mapSize = radius * 2;

            svgContainer.style.minWidth = "100%";
            svgContainer.style.width = mapSize;
            svgContainer.setAttribute("viewBox", "0 0 " + mapSize + " " + mapSize);

            const edge = createSvgElement("circle");
                edge.setAttribute("r", radius);
                edge.setAttribute("cx", radius);
                edge.setAttribute("cy", radius);
                edge.setAttribute("fill", "transparent");
                edge.setAttribute("stroke", "white");
                edge.setAttribute("stroke-width", 10);
        svgContainer.appendChild(edge);

            const star = createSvgElement("circle");
                star.setAttribute("r", solarSystemConstants.STAR_RADIUS);
                star.setAttribute("cx", radius);
                star.setAttribute("cy", radius);
                star.setAttribute("fill", "yellow");
        svgContainer.appendChild(star);


            new Stream(solarSystem.planets)
                .map(function(planet){return createPlanet(planet, solarSystem.radius)})
                .forEach(function(node){svgContainer.appendChild(node)});

            new Stream(solarSystem.planets)
                .map(function(planet){return createPlanetName(planet, solarSystem.radius)})
                .forEach(function(node){svgContainer.appendChild(node)});


        switchTab("main-tab", ids.solarSystem);

        const solarSystemContainer = document.getElementById(ids.solarSystemContainer);
        solarSystemContainer.scrollTop = (solarSystemContainer.scrollHeight - window.innerHeight) / 2;
        solarSystemContainer.scrollLeft = (solarSystemContainer.scrollWidth - window.innerWidth) / 2;

        function createPlanet(planet, systemRadius){
            const element = createSvgElement("circle");
                element.setAttribute("r", solarSystemConstants.PLANET_RADIUS);
                element.setAttribute("cx", solarSystemConstants.SOLAR_SYSTEM_BORDER_WIDTH + solarSystemConstants.OFFSET + systemRadius + planet.coordinate.x);
                element.setAttribute("cy", solarSystemConstants.SOLAR_SYSTEM_BORDER_WIDTH + solarSystemConstants.OFFSET + systemRadius + planet.coordinate.y);
                element.setAttribute("stroke", "blue");
                element.setAttribute("stroke-width", 0);
                $(element).hover(
                    function(){element.setAttribute("stroke-width", 3)},
                    function(){element.setAttribute("stroke-width", 0)}
                )
                element.onclick = function(){
                    //TODO
                }
            return element;
        }

        function createPlanetName(planet, systemRadius){
            const element = createSvgElement("text");
                element.classList.add("solar-system-planet-name-element");
                element.setAttribute("x", solarSystemConstants.SOLAR_SYSTEM_BORDER_WIDTH + solarSystemConstants.OFFSET + systemRadius + planet.coordinate.x);
                element.setAttribute("y", solarSystemConstants.SOLAR_SYSTEM_BORDER_WIDTH + solarSystemConstants.OFFSET + systemRadius + planet.coordinate.y - solarSystemConstants.PLANET_NAME_OFFSET);
                element.setAttribute("text-anchor", "middle");
                element.setAttribute("pointer-events", "none");
                element.innerHTML = planet.planetName;
            return element
        }
    }

    function init(){
        addRightClickMove(ids.solarSystemSvgContainer, ids.solarSystemContainer, false);
    }
})();