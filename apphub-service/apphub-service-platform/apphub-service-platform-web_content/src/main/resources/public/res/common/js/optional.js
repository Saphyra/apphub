function Optional(obj){
    const value = obj;

    this.ifPresent = function(consumer){
        if(this.isPresent()){
            consumer(value);
        }
    }

    this.ifNotPresent = function(func){
        if(!this.isPresent()){
            func();
        }
    }

    this.isPresent = function(){
        return value !== null && value !== undefined;
    }

    this.orElseGet = function(func){
        return this.isPresent() ? value : func();
    }
}