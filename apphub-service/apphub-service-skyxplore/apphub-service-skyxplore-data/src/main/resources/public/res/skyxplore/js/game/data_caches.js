(function Caches(){
    window.dataCaches = new function(){
        this.itemData = new Cache(itemDataLoader);
        this.itemDataNames = localization.loadCustomLocalization("skyxplore", "item_names");
        this.itemDataDescriptions = localization.loadCustomLocalization("skyxplore", "item_descriptions");
        this.surfaceTypeLocalization = localization.loadCustomLocalization("skyxplore", "surface_type");
        this.storageTypeLocalization = localization.loadCustomLocalization("skyxplore", "storage_type");
        this.skillTypeLocalization = localization.loadCustomLocalization("skyxplore", "skill_type");
        this.citizenStatLocalization = localization.loadCustomLocalization("skyxplore", "citizen_stat");
        this.terraformingPossibilities = new Cache(terraformingPossibilitiesLoader);
    }

    function itemDataLoader(itemId){
        let result;

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_ITEM_DATA", {dataId: itemId}));
            request.processValidResponse = function(response){
                const parsed = JSON.parse(response.body);
                console.log("Item loaded with id " + itemId, parsed);
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
                console.log("TerraformingPossibility loaded with surfaceType " + surfaceType, parsed);
                result = parsed;
            };
        const response = dao.sendRequest(request);

        return result;
    }
})();