/*
Response object contains the response status, statusKey, and text of the qiven request.
*/
function Response(response){
    response = response || {
        status: null,
        responseText: null
    };
    const statusKey = responseStatusMapper.getKeyOf(response.status);
    
    this.statusKey = statusKey;
    this.status = response.status;
    this.body = response.responseText;
    
    this.toString = function(){
        return this.status + ": " + this.statusKey + " - " + this.body;
    }
}