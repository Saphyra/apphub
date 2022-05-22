function parseLocalDate(date){
    if(typeof date !== "string"){
        throwException("IllegalArgument", date + " is not a string, it is a " + typeof date);
    }

    return new LocalDate(extractYear(date), extractMonth(date), extractDay(date));
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

function LocalDate(years, months, days){
    if(years instanceof Date){
        days = String(years.getDate()).padStart(2, '0');
        months = String(years.getMonth() + 1).padStart(2, '0'); //January is 0!
        years = years.getFullYear();
    }

    this.plusMonths = function(m){
        return new LocalDate(date.plusMonths(m));
    }

    this.minusMonths = function(m){
        return new LocalDate(date.minusMonths(m));
    }

    this.getMonth = function(){
        return months;
    }

    this.getYear = function(){
        return years;
    }

    this.getDay = function(){
        return days;
    }

    this.toString = function(){
        return years + "-" + months + "-" + days;
    }
}