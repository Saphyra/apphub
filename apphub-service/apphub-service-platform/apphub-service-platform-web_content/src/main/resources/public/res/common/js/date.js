Date.isLeapYear = function (year) {
    return (((year % 4 === 0) && (year % 100 !== 0)) || (year % 400 === 0));
};

Date.getDaysInMonth = function (year, month) {
    return [31, (Date.isLeapYear(year) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
};

Date.prototype.isLeapYear = function () {
    return Date.isLeapYear(this.getFullYear());
};

Date.prototype.getDaysInMonth = function () {
    return Date.getDaysInMonth(this.getFullYear(), this.getMonth());
};

Date.prototype.plusMonths = function (value) {
    const date = new Date(this.valueOf());
    const n = this.getDate();
    date.setDate(1);
    date.setMonth(this.getMonth() + value);
    date.setDate(Math.min(n, date.getDaysInMonth()));
    return date;
};

Date.prototype.minusMonths = function(months){
    const date = new Date(this.valueOf());
    const n = this.getDate();
    date.setDate(1);
    date.setMonth(date.getMonth() - months);
    date.setDate(Math.min(n, date.getDaysInMonth()));
    return date;
}

function formatDate(date){
    return date.getFullYear() + "-" + withLeadingZeros((date.getMonth() + 1), 2) + "-" + withLeadingZeros(date.getDate(), 2) + " " + date.toLocaleTimeString("hu");
}

window.LocalDateTime = new function(){
    this.fromEpochSeconds = function(epoch){
        const d = new Date(0);
        d.setUTCSeconds(epoch);
        return new LocalDateTimeObj(d);
    }

    function LocalDateTimeObj(date){
        if(!hasValue(date)){
            throwException("IllegalArgument", "date must not be null");
        }

        if(!(date instanceof Date)){
            throwException("IllegalArgument", "Date is not a Date");
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

        this.getHours = function(){
            return String(date.getHours()).padStart(2, '0');
        }

        this.getMinutes = function(){
            return String(date.getMinutes()).padStart(2, '0');
        }

        this.getSeconds = function(){
            return String(date.getSeconds()).padStart(2, '0');
        }

        this.toString = function(){
            return this.getYear() + "-" + this.getMonth() + "-" + this.getDay() + " " + this.getHours() + ":" + this.getMinutes() + ":" + this.getSeconds();
        }

        this.equals = function(obj){
            if(!hasValue(obj)){
                return false;
            }

            if(!obj instanceof LocalDateTimeObj){
                return false;
            }

            return obj.toString() == this.toString();
        }
    }
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