import React, { useEffect, useState } from "react";
import localizationData from "./language_localization.json";
import languages from "./languages.json";
import LocalizationHandler from "../../js/LocalizationHandler";
import Stream from "../../js/collection/Stream";
import Language from "./component/Language";
import "./language_selector.css";
import Utils from "../../js/Utils";

const LanguageSelector = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const [currentLanguage, setCurrentLanguage] = useState(localizationHandler.getLocale());

    const updateLanguage = (language) => {

        Utils.setCookie("language", language);
        window.location.reload();
    }

    const getLanguages = () => {
        return new Stream(languages)
            .map(language => <Language
                key={language}
                localizationHandler={localizationHandler}
                language={language}
                currentLanguage={currentLanguage}
                setCurrentLanguage={updateLanguage}
            />)
            .toList();
    }

    return (
        <div id="language-selector">
            {getLanguages()}
        </div>
    );
}

export default LanguageSelector;