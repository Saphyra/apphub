import MapStream from "./collection/MapStream";
import Optional from "./collection/Optional";
import Constants from "./Constants";
import { getBrowserLanguage, getCookie, hasValue } from "./Utils";

const LocalizationHandler = class {
    constructor(localization) {
        this.localization = localization;
    }

    getKeys() {
        return new MapStream(this.localization)
            .toList(key => key);
    }

    get(key, params = {}) {
        return this.getOptional(key, params)
            .orElseThrow("IllegalArgument", "No localization found for key " + key + " and locale " + this.getLocale());
    }

    getOrDefault(key, defaultValue, params = {}) {
        return this.getOptional(key, params)
            .orElse(defaultValue);
    }

    getOptional(key, params) {
        const locale = this.getLocale();

        const item = this.localization[key];

        if (!item) {
            return new Optional();
        }

        let result = item[locale];

        if (!result) {
            new Optional(result);
        }

        new MapStream(params)
            .forEach((key, value) => result = result.replace("{" + key + "}", this.getParamValue(item, key, value)));

        return new Optional(result);
    }

    getParamValue(item, key, value){
        if(!hasValue(item.params) || !hasValue(item.params[key])){
            return value;
        }

        return new LocalizationHandler(item.params[key])
            .get(value);
    }

    getLocale() {
        return getCookie(Constants.COOKIE_LOCALE) || getBrowserLanguage() || Constants.DEFAULT_LOCALE;
    }
}

export default LocalizationHandler;