(function DateTimeFormatter(){
    window.dateTimeFormatter = new function(){
        this.convertTimeStamp = convertTimeStamp;
        this.formatEpoch = formatEpoch;
    }

    function formatEpoch(epoch){
        const date = new Date(0);
            date.setUTCSeconds(epoch);

        const months = (date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1);
        const days = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
        const hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
        const seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();

        return date.getFullYear()
            + "/" + months
            + "/" + days
            + " " + hours
            + ":" + seconds;
    }

    function convertTimeStamp(timeStamp){
        const hours = Math.floor(timeStamp / 3600);
        const minutes = Math.floor((timeStamp - hours * 3600) / 60);
        const seconds = timeStamp - hours * 3600 - minutes * 60;

        return new Time(hours, minutes, seconds);

        function Time(hours, minutes, seconds){
            this.hours = hours || 0;
            this.minutes = minutes || 0;
            this.seconds = seconds || 0;

            this.toString = function(){
                return (this.hours < 10 ? "0" : "") + this.hours + ":"
                + (this.minutes < 10 ? "0" : "") + this.minutes + ":"
                + (this.seconds < 10 ? "0" : "") + this.seconds;
            }
        }
    }
})();