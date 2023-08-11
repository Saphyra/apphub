import React from "react";
import Button from "../../input/Button";

const Language = ({ localizationHandler, language, currentLanguage, setCurrentLanguage }) => {
    return (
        <Button
            className={"language " + language + (currentLanguage === language ? " current" : "")}
            title={localizationHandler.get(language)}
            onclick={() => setCurrentLanguage(language)}
        />
    );
}

export default Language;