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
        const request = new Request(Mapping.getEndpoint("MODULES_GET_MODULES_OF_USER"));
            request.convertResponse = responseConverter;
            request.processValidResponse = function(categories){
                displayAll(categories);
                displayFavorites(categories);
            }
        dao.sendRequestAsync(request);
    }

    function responseConverter(response){
        return new MapStream(JSON.parse(response.body))
            .sorted(function(a, b){return categoryNames.get(a.getKey()).localeCompare(categoryNames.get(b.getKey()))})
            .map(function(category, modules){
                return new Stream(modules)
                    .sorted(function(a, b){return moduleNames.get(a.name).localeCompare(moduleNames.get(b.name))})
                    .toList();
            })
            .toMap();
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
                const categoryNode = createCategoryNode(category);
                    categoryNode.id="category-" + category;

                    const modulesContainer = document.createElement("div");
                        new Stream(modules)
                            .map(function(module){
                                const moduleNode = createModuleNode(module, createMarkFavoriteFunction(module, !module.favorite));
                                    moduleNode.id = "module-" + module.name;
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
                const categoryNode = createCategoryNode(category);

                    const modulesContainer = document.createElement("div");
                        new Stream(modules)
                            .map(function(module){
                                return createModuleNode(module, createMarkFavoriteFunction(module, false));
                            })
                            .forEach(function(node){modulesContainer.appendChild(node)});
                categoryNode.appendChild(modulesContainer);
                return categoryNode;
            })
            .toListStream()
            .forEach(function(node){listContainer.appendChild(node)});
    }

    function createCategoryNode(category){
        const categoryNode = document.createElement("div");
            categoryNode.classList.add("category");

            const categoryNameLabel = document.createElement("div");
                categoryNameLabel.classList.add("category-name");
                categoryNameLabel.innerHTML = categoryNames.get(category);
        categoryNode.appendChild(categoryNameLabel);
        return categoryNode
    }

    function createModuleNode(module, callback){
        const moduleNode = document.createElement("div");
            moduleNode.classList.add("module");
            const favoriteButton = document.createElement("div");
                favoriteButton.classList.add("favorite-button");
                favoriteButton.classList.add("button");
                favoriteButton.onclick = callback;
                favoriteButton.classList.add(module.favorite ? "favorite" : "non-favorite");
        moduleNode.appendChild(favoriteButton);

            const moduleLink = document.createElement("a");
                moduleLink.classList.add("module-link");
                moduleLink.innerHTML = moduleNames.get(module.name);
                moduleLink.href = module.url;
        moduleNode.appendChild(moduleLink);
        return moduleNode;
    }

    function createMarkFavoriteFunction(module, value){
        return function(){
            const request = new Request(Mapping.getEndpoint("MODULES_SET_FAVORITE", {module: module.name}), {value: value});
                request.convertResponse = responseConverter;
                request.processValidResponse = function(categories){
                    displayAll(categories);
                    displayFavorites(categories);
                }
            dao.sendRequestAsync(request);
        }
    }
})();