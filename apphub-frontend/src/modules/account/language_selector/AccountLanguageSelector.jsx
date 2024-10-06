import React from "react";
import localizationData from "./account_language_selector_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import LanguageSelector from "../../../common/component/language_selector/LanguageSelector";
import { ACCOUNT_CHANGE_LANGUAGE } from "../../../common/js/dao/endpoints/UserEndpoints";

const AccountLanguageSelector = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const setLanguage = async (language) => {
        await ACCOUNT_CHANGE_LANGUAGE.createRequest({ value: language })
            .send();

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