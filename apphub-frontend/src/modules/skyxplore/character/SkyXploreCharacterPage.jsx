import React, { useEffect, useState } from "react";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import localizationData from "./page_localization.json";
import Redirection from "../Redirection";
import Header from "../../../common/component/Header"
import Footer from "../../../common/component/Footer"
import Endpoints from "../../../common/js/dao/dao";
import { ToastContainer } from "react-toastify";
import Button from "../../../common/component/input/Button";
import Constants from "../../../common/js/Constants";
import "./character_page.css";
import PreLabeledInputField from "../../../common/component/input/PreLabeledInputField";
import ValidationInputField from "../../../common/component/input/ValidatedInputField";
import ValidatedField from "../../../common/js/validation/ValidatedField";
import validate from "../../../common/js/validation/Validator";
import "../skyxplore.css";

const SkyXploreCharacterPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [hasCharacter, setHasCharacter] = useState(false);
    const [characterName, setCharacterName] = useState("");
    const [validationResult, setValidationResult] = useState({});

    useEffect(() => checkRedirection(), []);
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => fetchCharacterExistence(), []);
    useEffect(() => prefillCharacterName(), [hasCharacter]);
    useEffect(() => runValidation(), [characterName]);

    const checkRedirection = () => {
        Redirection.forCharacter()
    }

    const fetchCharacterExistence = () => {
        const fetch = async () => {
            const response = await Endpoints.SKYXPLORE_PLATFORM_HAS_CHARACTER.createRequest()
                .send();
            setHasCharacter(response.value);
        }
        fetch();
    }

    const prefillCharacterName = () => {
        const fetchCharacterName = async () => {
            const response = await Endpoints.SKYXPLORE_GET_CHARACTER_NAME.createRequest()
                .send();
            setCharacterName(response.value);
        }
        const fetchUsername = async () => {
            const response = await Endpoints.USER_DATA_GET_USERNAME.createRequest()
                .send();
            setCharacterName(response.value);
        }
        hasCharacter ? fetchCharacterName() : fetchUsername();
    }

    const runValidation = () => {
        const fields = {};
        fields[ValidatedField.CHARACTER_NAME] = characterName;
        setValidationResult(validate(fields, localizationHandler));
    }

    const isFormValid = () => {
        return Object.values(validationResult)
            .every((validation) => validation.valid);
    }

    const saveCharacter = async () => {
        const request = {
            name: characterName
        }

        await Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER.createRequest(request)
            .send();

        window.location.href = Constants.SKYXPLORE_MAIN_MENU_PAGE;
    }

    const backButton = <Button
        key={"back-button"}
        id={"back-button"}
        label={localizationHandler.get("back-button")}
        onclick={() => window.location.href = hasCharacter ? Constants.SKYXPLORE_MAIN_MENU_PAGE : Constants.MODULES_PAGE}
    />

    return (
        <div>
            <div className="skyxplore-background" />
            <div id="skyxplore-character" className="main-page skyxplore-main">
                <Header label={localizationHandler.get("page-title")} />

                <main>
                    <div id="skyxplore-character-details">
                        <h2 id="skyxplore-character-details-title">{localizationHandler.get(hasCharacter ? "edit-character" : "new-character")}</h2>

                        <div id="skyxplore-character-details-content-wrapper">
                            <PreLabeledInputField
                                label={localizationHandler.get("character-name") + ":"}
                                input={
                                    <ValidationInputField
                                        id="skyxplore-character-name-validation"
                                        inputId="skyxplore-character-name"
                                        validationResult={validationResult[ValidatedField.CHARACTER_NAME]}
                                        type="text"
                                        placeholder={localizationHandler.get("character-name")}
                                        onchangeCallback={setCharacterName}
                                        value={characterName}
                                    />
                                }
                            />

                            <Button
                                id="save-character-button"
                                label={localizationHandler.get("save-character")}
                                onclick={saveCharacter}
                                disabled={!isFormValid()}
                            />
                        </div>

                    </div>
                </main>

                <Footer leftButtons={[backButton]} />

                <ToastContainer />
            </div>
        </div>
    );
}

export default SkyXploreCharacterPage;