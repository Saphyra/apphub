window.LocalDate = new function(){
    this.parse = function(dateString){
        return new LocalDateObj(new Date(extractYear(dateString), extractMonth(dateString) - 1, extractDay(dateString)));
    }

    this.create = function(date){
        return new LocalDateObj(date);
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
        if(!date instanceof Date){
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
            return date.getFullYear();;
        }

        this.getDay = function(){
            return String(date.getDate()).padStart(2, '0');
        }

        this.toString = function(){
            return this.getYear() + "-" + this.getMonth() + "-" + this.getDay();
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