window.Mapping = {
    LOGOUT: "/api/logout",

    concat: function(path, id){
        return path.replace("*", id);
    },

    replace: function(path, pathVariables){
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