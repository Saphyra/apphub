function CustomLocalization(fileName){
    const localization = loadLocalization(fileName, function(localization){return localization});

    this.getKeys = function(){
        return Object.keys(localization);
    }

    this.get = function(key){
        return localization[key] || function(){
            const message = "Localization not found with key " + key + " in file " + fileName;
            logService.log(message, "warn", "IllegalArgument");
            return message;
        }()
    }
}