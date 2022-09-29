function Optional(obj){
    const value = obj;

    this.ifPresent = function(consumer, fallBack){
        if(this.isPresent()){
            consumer(value);
        }else if(fallBack){
            fallBack();
        }
        return this;
    }

    this.ifNotPresent = function(func){
        if(!this.isPresent()){
            func();
        }
    }

    this.isPresent = function(){
        return value !== null && value !== undefined;
    }

    this.map = function(mapper){
        return this.isPresent() ? new Optional(mapper(value)) : this;
    }

    this.orElse = function(elseValue){
        return this.isPresent() ? value : elseValue;
    }

    this.orElseGet = function(func){
        return this.isPresent() ? value : func();
    }

    this.orElseThrow = function(errorType, errorMessage){
        if(this.isPresent()){
            return value;
        }

        throwException(errorType, errorMessage);
    }
}