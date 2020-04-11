window.HttpMethod = new function(){
    this.GET = "GET";
    this.POST = "POST";
    this.PUT = "PUT";
    this.DELETE = "DELETE";
    
    this.allowedMethods = [this.GET, this.POST, this.PUT, this.DELETE];
}