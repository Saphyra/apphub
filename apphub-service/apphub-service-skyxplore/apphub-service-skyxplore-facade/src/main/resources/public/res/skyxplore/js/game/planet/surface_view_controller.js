(function SurfaceViewController(){
    window.surfaceViewController = new function(){
        this.loadSurface = loadSurface;
    }

    function loadSurface(planetId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_PLANET_SURFACE", {planetId: planetId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(surfaces){
                displaySurfaces(surfaces);
            }
        dao.sendRequestAsync(request);
    }

    function displaySurfaces(surfaces){
        const surfaceContainer = document.getElementById(ids.planetSurfaceContainer);
            surfaceContainer.innerHTML = "";

        const coordinateMapping = createCoordinateMapping(surfaces);

        for(let rowIndex in coordinateMapping){
            const rowNode = document.createElement("DIV");
                rowNode.classList.add("surface-table-row");

                const row = coordinateMapping[rowIndex];

                for(let columnIndex in row){
                    const surface = row[columnIndex];

                    const surfaceNode = document.createElement("SPAN");
                        surfaceNode.classList.add("surface-table-cell");
                        surfaceNode.classList.add("surface-type-" + surface.surfaceType.toLowerCase());
                    rowNode.appendChild(surfaceNode);
                }
            surfaceContainer.appendChild(rowNode);
        }

        function createCoordinateMapping(surfaces){
            const result = {};

            for(let index in surfaces){
                const surface = surfaces[index];
                const coordinate = surface.coordinate;
                if(result[coordinate.x] == undefined){
                    result[coordinate.x] = {};
                }

                result[coordinate.x][coordinate.y] = surface;
            }

            return result;
        }
    }
})();