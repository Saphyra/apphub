(function SolarSystemController(){
    window.solarSystemConstants = {
        SOLAR_SYSTEM_BORDER_WIDTH: 10,
        OFFSET: 70,
        STAR_RADIUS: 40,
        PLANET_RADIUS: 20,
        PLANET_NAME_OFFSET: 30,
    }

    let openedSolarSystemId = null;
    let currentSolarSystemName;

    pageLoader.addLoader(function(){addRightClickMove(ids.solarSystemSvgContainer, ids.solarSystemContainer, false)}, "SolarSystem add rightClickMove");
    pageLoader.addLoader(solarSystemRenaming, "SolarSystem renaming");

    window.solarSystemController = new function(){
        this.viewSolarSystem = viewSolarSystem;
        this.getOpenedSolarSystemId = function(){
            return openedSolarSystemId;
        }
    }

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
        currentSolarSystemName = solarSystem.systemName;
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
                .map(function(planet){return createPlanet(planet, solarSystem.radius, solarSystem.solarSystemId)})
                .forEach(function(node){svgContainer.appendChild(node)});

            new Stream(solarSystem.planets)
                .map(function(planet){return createPlanetName(planet, solarSystem.radius)})
                .forEach(function(node){svgContainer.appendChild(node)});


        switchTab("main-tab", ids.solarSystem);

        const solarSystemContainer = document.getElementById(ids.solarSystemContainer);
        solarSystemContainer.scrollTop = (solarSystemContainer.scrollHeight - window.innerHeight) / 2;
        solarSystemContainer.scrollLeft = (solarSystemContainer.scrollWidth - window.innerWidth) / 2;

        function createPlanet(planet, systemRadius, solarSystemId){
            const element = createSvgElement("circle");
                element.setAttribute("r", solarSystemConstants.PLANET_RADIUS);
                element.setAttribute("cx", solarSystemConstants.SOLAR_SYSTEM_BORDER_WIDTH + solarSystemConstants.OFFSET + systemRadius + planet.coordinate.x);
                element.setAttribute("cy", solarSystemConstants.SOLAR_SYSTEM_BORDER_WIDTH + solarSystemConstants.OFFSET + systemRadius + planet.coordinate.y);
                element.setAttribute("stroke", "blue");
                element.setAttribute("stroke-width", 0);
                element.classList.add("planet-svg-element");
                $(element).hover(
                    function(){element.setAttribute("stroke-width", 3)},
                    function(){element.setAttribute("stroke-width", 0)}
                )
                element.onclick = function(){
                    planetController.viewPlanet(planet.planetId);
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
    
    function solarSystemRenaming(){
        const solarSystemNameField = document.getElementById(ids.solarSystemName);

        $(solarSystemNameField).on("keyup keypress", function(e){
            if(e.which == 13){
                e.preventDefault();
            }
        })

        solarSystemNameField.onclick = function(){
            solarSystemNameField.contentEditable = true;
            solarSystemNameField.focus();
        }

        solarSystemNameField.addEventListener("focusin", function(){selectElementText(solarSystemNameField)});

        solarSystemNameField.addEventListener("focusout", function(){
            const newName = solarSystemNameField.innerHTML;

            if(newName == currentSolarSystemName){
                solarSystemNameField.contentEditable = false;
                return;
            }

            if(isBlank(newName)){
                solarSystemNameField.innerText = currentSolarSystemName;
                solarSystemNameField.contentEditable = false;
            } else if(newName.length > 30){
                solarSystemNameField.innerText = currentSolarSystemName;
                solarSystemNameField.contentEditable = false;
                notificationService.showError(Localization.getAdditionalContent("new-solar-system-name-too-long"));
            } else{
                const request = new Request(Mapping.getEndpoint("SKYXPLORE_SOLAR_SYSTEM_RENAME", {solarSystemId: openedSolarSystemId}), {value: newName});
                    request.processValidResponse = function(){
                        currentSolarSystemName = newName;
                        solarSystemNameField.contentEditable = false;
                    }
                    request.processInvalidResponse = function(){
                        solarSystemNameField.innerText = currentSolarSystemName;
                        solarSystemNameField.contentEditable = false;
                    }
                dao.sendRequestAsync(request);
            }
        });
    }
})();