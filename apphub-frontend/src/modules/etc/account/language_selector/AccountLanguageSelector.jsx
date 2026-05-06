import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./account_language_selector_localization.json";
import LanguageSelector from "common/component/language_selector/LanguageSelector";
import { ACCOUNT_CHANGE_LANGUAGE } from "../AccountEndpoints";
import Constants from "common/js/Constants";

const AccountLanguageSelector = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const setLanguage = async (language) => {
        await ACCOUNT_CHANGE_LANGUAGE.createRequest({ value: language })
            .send();

        localStorage[Constants.STORAGE_KEY_LOCALE] = language;
        window.location.reload();
    }

    return (
        <div className="account-tab-wrapper">
            <div className="account-tab">
                <div className="account-tab-title">{localizationHandler.get("tab-title")}</div>

                <div className="account-tab-content">
                    <LanguageSelector
                        currentLanguage={localizationHandler.getLocale()}
                        updateCallback={setLanguage}
                    />

                </div>
            </div>
        </div>
    );
}

export default AccountLanguageSelector;