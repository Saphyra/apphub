import React from "react";
import localizationData from "./language_localization.json";
import languages from "./languages.json";
import LocalizationHandler from "../../js/LocalizationHandler";
import Stream from "../../js/collection/Stream";
import Language from "./component/Language";
import "./language_selector.css";

const LanguageSelector = ({ currentLanguage, updateCallback }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const getLanguages = () => {
        return new Stream(languages)
            .map(language => <Language
                key={language}
                localizationHandler={localizationHandler}
                language={language}
                currentLanguage={currentLanguage}
                setCurrentLanguage={updateCallback}
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