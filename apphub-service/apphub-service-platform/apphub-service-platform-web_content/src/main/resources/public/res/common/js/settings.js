function Settings(category){
    let items = null;

    this.initialize = function(){
        const request = new Request(Mapping.getEndpoint("GET_USER_SETTINGS", {category: category}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(s){
                items = s;
            }
        return dao.sendRequestAsync(request);
    }

    this.get = function(key){
        const result = items[key];
        if(result == undefined){
            throwException("IllegalArgument", "No setting present with key " + key + " in category " + category);
        }
        return result;
    }

    this.set = function(key, value){
        const payload = {
            category: category,
            key: key,
            value: value
        }

        const request = new Request(Mapping.getEndpoint("SET_USER_SETTINGS"), payload);
            request.processValidResponse = function(){
                items[key] = value;
            };
        return dao.sendRequestAsync(request);
    }
};