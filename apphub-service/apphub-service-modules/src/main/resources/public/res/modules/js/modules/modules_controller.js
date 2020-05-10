(function ModulesController(){
    scriptLoader.loadScript("/res/common/js/localization/custom_localization.js");

    const categoryNames = new CustomLocalization("modules", "module_category");
    const moduleNames = new CustomLocalization("modules", "module_name");

    let searchTimeout = null;

    window.modulesController = new function(){
        this.displayModules = displayModules;
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.SEARCH_ATTEMPT},
        function(){
            if(searchTimeout){
                clearTimeout(searchTimeout);
            }

            searchTimeout = setTimeout(displayModules, 1000);
        }
    ))

    function displayModules(){
        const request = new Request(Mapping.getEndpoint("GET_MODULES"));
            request.convertResponse = function(response){
                return new MapStream(JSON.parse(response.body))
                    .sorted(function(a, b){return categoryNames.get(a.getKey()).localeCompare(categoryNames.get(b.getKey()))})
                    .map(function(category, modules){
                        return new Stream(modules)
                            .sorted(function(a, b){return moduleNames.get(a.name).localeCompare(moduleNames.get(b.name))})
                            .toList();
                    })
                    .toMap();
            }
            request.processValidResponse = function(categories){
                displayAll(categories);
                displayFavorites(categories);
            }
        dao.sendRequestAsync(request);
    }

    function displayAll(categories){
        const searchValue = $("#search-field").val().toLowerCase();
        const listContainer = document.getElementById("all-modules-list");
            listContainer.innerHTML = "";

        const categoriesToDisplay = new MapStream(categories)
            .map(function(category, modules){
                if(categoryNames.get(category).toLowerCase().includes(searchValue)){
                    return modules;
                }

                return new Stream(modules)
                    .filter(function(module){return moduleNames.get(module.name).toLowerCase().includes(searchValue)})
                    .toList();
            })
            .filter(function(category, modules){return modules.length > 0})
            .toMap();

        new MapStream(categoriesToDisplay)
            .map(function(category, modules){
                const categoryNode = document.createElement("div");

                    const categoryNameLabel = document.createElement("div");
                        categoryNameLabel.innerHTML = categoryNames.get(category);
                categoryNode.appendChild(categoryNameLabel);

                    const modulesContainer = document.createElement("ul");
                        new Stream(modules)
                            .map(function(module){
                                const moduleNode = document.createElement("li");
                                    const moduleLink = document.createElement("a");
                                        moduleLink.innerHTML = moduleNames.get(module.name);
                                        moduleLink.href = module.url;
                                        moduleLink.target = "_blank";
                                moduleNode.appendChild(moduleLink);
                                //TODO add mark as favorite button
                                return moduleNode;
                            })
                            .forEach(function(node){modulesContainer.appendChild(node)});
                categoryNode.appendChild(modulesContainer);
                return categoryNode;
            })
            .toListStream()
            .forEach(function(node){listContainer.appendChild(node)});
    }

    function displayFavorites(categories){
        const listContainer = document.getElementById("favorites-list");
            listContainer.innerHTML = "";

        const categoriesToDisplay = new MapStream(categories)
            .map(function(category, modules){
                return new Stream(modules)
                    .filter(function(module){return module.favorite})
                    .toList();
            })
            .filter(function(category, modules){return modules.length > 0})
            .toMap();

        new MapStream(categoriesToDisplay)
            .map(function(category, modules){
                const categoryNode = document.createElement("div");

                    const categoryNameLabel = document.createElement("div");
                        categoryNameLabel.innerHTML = categoryNames.get(category);
                categoryNode.appendChild(categoryNameLabel);

                    const modulesContainer = document.createElement("ul");
                        new Stream(modules)
                            .map(function(module){
                                const moduleNode = document.createElement("li");
                                    const moduleLink = document.createElement("a");
                                        moduleLink.innerHTML = moduleNames.get(module.name);
                                        moduleLink.href = module.url;
                                        moduleLink.target = "_blank";
                                moduleNode.appendChild(moduleLink);
                                //TODO add unmark as favorite button
                                return moduleNode;
                            })
                            .forEach(function(node){modulesContainer.appendChild(node)});
                categoryNode.appendChild(modulesContainer);
                return categoryNode;
            })
            .toListStream()
            .forEach(function(node){listContainer.appendChild(node)});
    }
})();