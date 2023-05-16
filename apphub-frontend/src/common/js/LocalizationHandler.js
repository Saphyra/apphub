import MapStream from "./collection/MapStream";
import Constants from "./Constants";
import Utils from "./Utils";

const LocalizationHandler = class {
    constructor(localization) {
        this.localization = localization;
    }

    get(key, params = {}) {
        const locale = this.getLocale();

        const item = this.localization[key];

        if (!item) {
            Utils.throwException("IllegalArgument", key + " is not found.");
        }

        let result = item[locale];

        if (!result) {
            Utils.throwException("IllegalArgument", "No localization found for key " + key + " and locale " + locale);
        }

        new MapStream(params)
            .forEach((key, value) => result = result.replace("{" + key + "}", value));

        return result;
    }

    getLocale() {
        return Utils.getCookie(Constants.COOKIE_LOCALE) || Utils.getBrowserLanguage() || Constants.DEFAULT_LOCALE;
    }
}

export default LocalizationHandler;