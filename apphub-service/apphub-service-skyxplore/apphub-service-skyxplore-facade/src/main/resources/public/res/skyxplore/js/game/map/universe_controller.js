(function UniverseController(){
    window.universeController = new function(){
        this.loadUniverse = loadUniverse;
    }

    function loadUniverse(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_UNIVERSE"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(universe){
                displayUniverse(universe);
            }
        dao.sendRequestAsync(request);
    }

    function displayUniverse(universe){
        const svgContainer = document.getElementById(ids.mapSvgContainer);
            const mapWidth = universe.universeSize + mapConstants.X_OFFSET * 2;
            const mapHeight = universe.universeSize + mapConstants.Y_OFFSET * 2;

            svgContainer.style.minWidth = "100%";
            svgContainer.style.width = mapWidth;
            svgContainer.setAttribute("viewBox", "0 0 " + mapWidth + " " + mapHeight);

        new Stream(universe.connections)
            .map(createConnection)
            .forEach(function(node){svgContainer.appendChild(node)});

        new Stream(universe.solarSystems)
            .map(createSolarSystem)
            .forEach(function(node){svgContainer.appendChild(node)});

        new Stream(universe.solarSystems)
            .map(createSolarSystemName)
            .forEach(function(node){svgContainer.appendChild(node)});

        function createConnection(connection){
            const element = createSvgElement("line");
                element.setAttribute("x1", connection.a.x + mapConstants.X_OFFSET);
                element.setAttribute("y1", connection.a.y + mapConstants.Y_OFFSET);
                element.setAttribute("x2", connection.b.x + mapConstants.X_OFFSET);
                element.setAttribute("y2", connection.b.y + mapConstants.Y_OFFSET);
                element.setAttribute("stroke", "white");
                element.setAttribute("stroke-width", 1);
            return element;
        }

        function createSolarSystem(solarSystem){
            const element = createSvgElement("circle");
                element.setAttribute("r", mapConstants.STAR_SIZE);
                element.setAttribute("cx", solarSystem.coordinate.x + mapConstants.X_OFFSET);
                element.setAttribute("cy", solarSystem.coordinate.y + mapConstants.Y_OFFSET);
                element.setAttribute("stroke", "blue");
                element.setAttribute("stroke-width", 0);
                $(element).hover(
                    function(){element.setAttribute("stroke-width", 3)},
                    function(){element.setAttribute("stroke-width", 0)}
                )
                element.onclick = function(){
                    //TODO open system view
                }
            return element;
        }

        function createSolarSystemName(solarSystem){
            const element = createSvgElement("text");
                element.classList.add("map-solar-system-name-element");
                element.setAttribute("x", solarSystem.coordinate.x + mapConstants.X_OFFSET);
                element.setAttribute("y", solarSystem.coordinate.y - mapConstants.STAR_NAME_OFFSET + mapConstants.Y_OFFSET);
                element.setAttribute("text-anchor", "middle");
                element.setAttribute("pointer-events", "none");
                element.innerText = solarSystem.solarSystemName + " (" + solarSystem.planetNum + ")";
            return element;
        }
    }
})();