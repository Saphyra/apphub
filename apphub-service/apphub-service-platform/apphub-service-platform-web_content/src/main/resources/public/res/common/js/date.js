Date.prototype.plusMonths = function(months){
    const date = new Date(this.valueOf());
    date.setMonth(date.getMonth() + months);
    return date;
}

Date.prototype.minusMonths = function(months){
    const date = new Date(this.valueOf());
    date.setMonth(date.getMonth() - months);
    return date;
}

function formatDate(date){
    return date.getFullYear() + "-" + date.getMonth() + "-" + date.getDate() + " " + date.toLocaleTimeString(getLocale());
}

window.LocalDate = new function(){
    this.parse = function(dateString){
        return new LocalDateObj(new Date(extractYear(dateString), extractMonth(dateString) - 1, extractDay(dateString)));
    }

    this.create = function(date){
        return new LocalDateObj(date);
    }

    this.now = function(){
        return this.create(new Date());
    }

    function extractDay(date){
        return date.split("-")[2];
    }

    function extractMonth(date){
        return date.split("-")[1];
    }

    function extractYear(date){
        return date.split("-")[0];
    }

    function LocalDateObj(date){
        if(!hasValue(date)){
            throwException("IllegalArgument", "date must not be null");
        }

        if(!(date instanceof Date)){
            console.log(date);
            throwException("IllegalArgument", "Date is not a Date");
        }

        this.plusMonths = function(m){
            return new LocalDateObj(date.plusMonths(m));
        }

        this.minusMonths = function(m){
            return new LocalDateObj(date.minusMonths(m));
        }

        this.getMonth = function(){
            return String(date.getMonth() + 1).padStart(2, '0'); //January is 0!
        }

        this.getYear = function(){
            return date.getFullYear();
        }

        this.getDay = function(){
            return String(date.getDate()).padStart(2, '0');
        }

        this.toString = function(){
            return this.getYear() + "-" + this.getMonth() + "-" + this.getDay();
        }

        this.isSameMonth = function(obj){
            if(!obj instanceof LocalDateObj){
                console.log(obj);
                throwException("IllegalArgument", "obj is not a LocalDateObj");
            }

            return this.getYear() == obj.getYear() && this.getMonth() == obj.getMonth();
        }

        this.equals = function(obj){
            if(!hasValue(obj)){
                return false;
            }

            if(!obj instanceof LocalDateObj){
                return false;
            }

            return obj.toString() == this.toString();
        }
    }
}