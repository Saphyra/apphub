const Optional = class{
    constructor(value){
        this.value = value;
    }

    orElse(another){
        if(this.value === null || this.value === undefined){
            return another;
        }

        return this.value;
    }
}

export default Optional;