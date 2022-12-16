function createSuccessProcess(id){
    return function(){
        if(!id.startsWith("#")){
            id = "#" + id;
        }
        console.log("Running successProcess for id " + id);
        $(id).fadeOut();
    }
}

function createErrorProcess(id, code){
    return function errorProcess(){
        if(!id.startsWith("#")){
            id = "#" + id;
        }

        console.log("Running errorProcess for id " + id + " and code " + code);
        $(id).prop("title", localization.getAdditionalContent(code))
            .fadeIn();
    }
}