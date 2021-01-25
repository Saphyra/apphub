function Stream(a){
    if(a == null || a == undefined){
        throwException("IllegalArgument", "Input must not be null or undefined.");
    }
    const array = a;

    this.allMatch = function(predicate){
        for(let i in array){
            if(!predicate(array[i])){
                return false;
            }
        }

        return true;
    }

    this.distinct = function(){
        const newArr = array.filter(function(value, index, self){
            return self.indexOf(value) === index
        });
        return new Stream(newArr);
    }

    this.findFirst = function(){
        return array[0];
    }

    this.flatMap = function(mappingFunction){
        const newArray = [];

        this.forEach(function(item){
            const res = mappingFunction(item);
            res.forEach(function(r){newArray.push(r)});
        });

        return new Stream(newArray);
    }

    this.filter = function(filterMethod){
        const result = [];

        this.forEach(function(item){
            if(filterMethod(item)){
                result.push(item);
            }
        })

        return new Stream(result);
    }

    this.forEach = function(consumer){
        for(let i = 0; i < array.length; i++){
            consumer(array[i]);
        }
    }

    this.groupBy = function(keyMapper){
        const result = {};

        this.forEach(function(item){
            const key = keyMapper(item);
            if(!result[key]){
                result[key] = [];
            }
            result[key].push(item);
        });

        return new MapStream(result);
    }

    this.map = function(mapper){
        const buff = [];

        for(let i in array){
            buff.push(mapper(array[i]));
        }
        return new Stream(buff);
    }

    this.anyMatch = function(predicate){
        for(let i in array){
            if(predicate(array[i])){
                return true;
            }
        }

        return false;
    }

    this.noneMatch = function(predicate){
        for(let i in array){
            if(predicate(array[i])){
                return false;
            }
        }

        return true;
    }

    this.peek = function(consumer){
        this.forEach(consumer);
        return this;
    }

    this.reverse = function(shouldReverse){
        if(!hasValue(shouldReverse) || shouldReverse){
            array.reverse();
        }

        return this;
    }

    this.sorted = function(comparator){
        array.sort(comparator);
        return this;
    }

    this.toList = function(){
        return array;
    }

    this.toMap = function(keyMapper, valueMapper){
        const result = {};

        this.forEach(function(item){
            result[keyMapper(item)] = valueMapper(item);
        })

        return result;
    }

    this.toMapStream = function(keyMapper, valueMapper){
        return new MapStream(this.toMap(keyMapper, valueMapper));
    }
}

function MapStream(i){
    const items = i || {};

    this.applyOnAllValues = function(consumer){
        consumer(items);
        return this;
    }

    this.filter = function(predicate){
        const values = {};
        this.forEach(function(key, value){
            if(predicate(key, value)){
                values[key] = value;
            }
        })
        return new MapStream(values);
    }

    this.forEach = function(consumer){
        for(let key in items){
            consumer(key, items[key]);
        }
    }

    this.map = function(valueMapper){
        const values = {};
        this.forEach(function(key, value){values[key] = valueMapper(key, value)});
        return new MapStream(values);
    }

    this.sorted = function(comparator){
        return new Stream(entryList(items))
            .sorted(comparator)
            .toMapStream(
                function(entry){return entry.getKey()},
                function(entry){return entry.getValue()}
            );
    }

    this.toEntryListStream = function(){
        return new Stream(entryList(items));
    }

    this.toListStream = function(mapper){
        if(mapper){
            const arr = [];
            this.forEach(function(key, value){arr.push(mapper(key, value))});
            return new Stream(arr);
        }else{
            return new Stream(Object.values(items));
        }
    }

    this.toMap = function(){
        return items;
    }
}

function entryList(map){
    const result = [];
    for(let key in map){
        result.push(new Entry(key, map[key]));
    }
    return result;
}

function orderMapByProperty(map, orderFunction){
    return new Stream(entryList(map))
        .sorted(orderFunction)
        .toMap(function(item){return item.getKey()}, function(item){return item.getValue()});
}

function Entry(k, v){
    const key = k;
    const value = v;

    this.getKey = function(){
        return key;
    }

    this.getValue = function(){
        return value;
    }
}