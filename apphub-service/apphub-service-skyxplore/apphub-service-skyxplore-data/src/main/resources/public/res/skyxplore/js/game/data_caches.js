(function Caches(){
    window.dataCaches = new function(){
        this.itemData = new Cache(itemDataLoader);
        this.itemDataNames = new CustomLocalization("skyxplore", "item_names");
        this.itemDataDescriptions = new CustomLocalization("skyxplore", "item_descriptions");
        this.surfaceTypeLocalization = new CustomLocalization("skyxplore", "surface_type");
        this.storageTypeLocalization = new CustomLocalization("skyxplore", "storage_type");
        this.skillTypeLocalization = new CustomLocalization("skyxplore", "skill_type");
        this.citizenStatLocalization = new CustomLocalization("skyxplore", "citizen_stat");
        this.terraformingPossibilities = new Cache(terraformingPossibilitiesLoader);
    }

    function itemDataLoader(itemId){
        let result;

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_ITEM_DATA", {dataId: itemId}));
            request.processValidResponse = function(response){
                const parsed = JSON.parse(response.body);
                logService.logToConsole("Item loaded with id " + itemId, parsed);
                result = parsed;
            };
        const response = dao.sendRequest(request);

        return result;
    }

    function terraformingPossibilitiesLoader(surfaceType){
        let result;

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_DATA_TERRAFORMING_POSSIBILITIES", {surfaceType: surfaceType}));
            request.processValidResponse = function(response){
                const parsed = JSON.parse(response.body);
                logService.logToConsole("TerraformingPossibility loaded with surfaceType " + surfaceType, parsed);
                result = parsed;
            };
        const response = dao.sendRequest(request);

        return result;
    }
})();