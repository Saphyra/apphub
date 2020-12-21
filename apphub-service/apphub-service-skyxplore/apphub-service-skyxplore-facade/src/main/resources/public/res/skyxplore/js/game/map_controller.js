(function MapController(){
    const X_OFFSET = 70;
    const Y_OFFSET = 70;
    const STAR_SIZE = 20;
    const STAR_NAME_OFFSET = 30;
    
    window.mapController = new function(){

    }

    $(document).ready(init);

    function loadMap(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_MAP"));
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
            const mapWidth = universe.universeSize + X_OFFSET * 2;
            const mapHeight = universe.universeSize + Y_OFFSET * 2;

            svgContainer.setAttribute("width", mapWidth);
            svgContainer.setAttribute("height", mapHeight);

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
                element.setAttribute("x1", connection.a.x + X_OFFSET);
                element.setAttribute("y1", connection.a.y + Y_OFFSET);
                element.setAttribute("x2", connection.b.x + X_OFFSET);
                element.setAttribute("y2", connection.b.y + Y_OFFSET);
                element.setAttribute("stroke", "white");
                element.setAttribute("stroke-width", 1);
            return element;
        }

        function createSolarSystem(solarSystem){
            const element = createSvgElement("circle");
                element.setAttribute("r", STAR_SIZE);
                element.setAttribute("cx", solarSystem.coordinate.x + X_OFFSET);
                element.setAttribute("cy", solarSystem.coordinate.y + Y_OFFSET);
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
                element.setAttribute("x", solarSystem.coordinate.x + X_OFFSET);
                element.setAttribute("y", solarSystem.coordinate.y - STAR_NAME_OFFSET + Y_OFFSET);
                element.setAttribute("text-anchor", "middle");
                element.setAttribute("pointer-events", "none");
                element.innerHTML = solarSystem.solarSystemName + " (" + solarSystem.planetNum + ")";
            return element;
        }
    }

    function init(){
        loadMap();
    }
})();