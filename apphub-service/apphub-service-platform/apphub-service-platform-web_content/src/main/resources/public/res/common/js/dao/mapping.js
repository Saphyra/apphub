window.Mapping = new function(){
    scriptLoader.loadScript("/res/common/js/dao/http_method.js");

    this.INDEX_PAGE = "/web";
    this.MODULES_PAGE = "/web/modules";

    const endpoints = {
        ADD_ROLE: new Endpoint("/api/user/data/roles", HttpMethod.PUT),
        ADMIN_PANEL_MENU: new Endpoint("/res/admin-panel/json/index_menu.json", HttpMethod.GET),
        AVAILABLE_ROLES: new Endpoint("/res/admin-panel/json/available_roles.json", HttpMethod.GET),
        CHANGE_EMAIL: new Endpoint("/api/user/account/email", HttpMethod.POST),
        CHANGE_LANGUAGE: new Endpoint("/api/user/account/language", HttpMethod.POST),
        CHANGE_PASSWORD: new Endpoint("/api/user/account/password", HttpMethod.POST),
        CHANGE_USERNAME: new Endpoint("/api/user/account/username", HttpMethod.POST),
        CHECK_SESSION: new Endpoint("/api/user/authentication/session", HttpMethod.GET),
        CREATE_NOTEBOOK_CATEGORY: new Endpoint("/api/notebook/category", HttpMethod.PUT),
        CREATE_NOTEBOOK_TEXT: new Endpoint("/api/notebook/text", HttpMethod.PUT),
        DELETE_ACCOUNT: new Endpoint("/api/user/account", HttpMethod.DELETE),
        DELETE_NOTEBOOK_LIST_ITEM: new Endpoint("/api/notebook/item/{listItemId}", HttpMethod.DELETE),
        GET_CHILDREN_OF_NOTEBOOK_CATEGORY: new Endpoint("/api/notebook/category/children", HttpMethod.GET),
        GET_LANGUAGES: new Endpoint("/api/user/data/languages", HttpMethod.GET),
        GET_MODULES: new Endpoint("/api/modules", HttpMethod.GET),
        GET_NOTEBOOK_CATEGORIES: new Endpoint("/api/notebook/category", HttpMethod.GET),
        GET_NOTEBOOK_TEXT: new Endpoint("/api/notebook/text/{textId}", HttpMethod.GET),
        GET_USER_ROLES: new Endpoint("/api/user/data/roles", HttpMethod.POST),
        LOGIN: new Endpoint("/api/user/authentication/login", HttpMethod.POST),
        LOGOUT: new Endpoint("/api/user/authentication/logout", HttpMethod.POST),
        MARK_AS_FAVORITE: new Endpoint("/api/modules/{module}/favorite", HttpMethod.POST),
        REGISTER: new Endpoint("/api/user/data", HttpMethod.POST),
        REMOVE_ROLE: new Endpoint("/api/user/data/roles", HttpMethod.DELETE),
    }

    this.getEndpoint = function(endpointName, pathVariables, queryParams){
        const ep = endpoints[endpointName] || throwException("IllegalArgument", "Endpoint not found with endpointName " + endpointName);
        return new Endpoint(
            replace(ep.getUrl(), pathVariables, queryParams),
            ep.getMethod()
        )
    }

     function replace (path, pathVariables, queryParams){
        let result = path;

        if(pathVariables){
            for(let index in pathVariables){
                if(pathVariables[index] != null){
                    const key = createKey(index);
                    result = result.replace(key, pathVariables[index]);
                }
            }
        }
        if(queryParams){
            result += "?";
            const paramParts = [];
            for (let index in queryParams){
                if(queryParams[index] != null){
                    paramParts.push(index + "=" + queryParams[index]);
                }
            }
            result += paramParts.join("&");
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