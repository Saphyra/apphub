window.Mapping = new function(){
    scriptLoader.loadScript("/res/common/js/dao/http_method.js");

    this.INDEX_PAGE = "/web";
    this.MODULES_PAGE = "/web/modules";

    const endpoints = {
        CHANGE_EMAIL: new Endpoint("/api/user/account/email", HttpMethod.POST),
        CHANGE_LANGUAGE: new Endpoint("/api/user/account/language", HttpMethod.POST),
        CHECK_SESSION: new Endpoint("/api/user/authentication/session", HttpMethod.GET),
        GET_LANGUAGES: new Endpoint("/api/user/data/languages", HttpMethod.GET),
        GET_MODULES: new Endpoint("/api/modules", HttpMethod.GET),
        LOGIN: new Endpoint("/api/user/authentication/login", HttpMethod.POST),
        LOGOUT: new Endpoint("/api/user/authentication/logout", HttpMethod.POST),
        MARK_AS_FAVORITE: new Endpoint("/api/modules/{module}/favorite", HttpMethod.POST),
        REGISTER: new Endpoint("/api/user/data", HttpMethod.POST)
    }

    this.getEndpoint = function(endpointName, pathVariables){
        const ep = endpoints[endpointName] || throwException("IllegalArgument", "Endpoint not found with endpointName " + endpointName);
        return new Endpoint(
            replace(ep.getUrl(), pathVariables),
            ep.getMethod()
        )
    }

     function replace (path, pathVariables){
        if(!pathVariables){
            return path;
        }
        let result = path;
        for(let index in pathVariables){
            const key = createKey(index);
            result = result.replace(key, pathVariables[index]);
        }

        return result;
        function createKey(index){
            return "{" + index + "}";
        }
    }
}

function Endpoint(u, m){
   const url = u;
   const method = m;

    this.getUrl = function(){
        return url;
   }

    this.getMethod = function(){
        return method;
    }
}