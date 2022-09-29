function CustomLocalization(module, fileName){
    console.log("Initializing CustomLocalization for module " + module + " and fileName " + fileName);

    const localization = loadLocalization(module, fileName, function(localization){return localization});

    this.getKeys = function(){
        return Object.keys(localization);
    }

    this.get = function(key){
        return localization[key] || function(){
            const message = "Localization not found with key " + key + " in file " + fileName;
            console.error(message);
            return message;
        }()
    }
}