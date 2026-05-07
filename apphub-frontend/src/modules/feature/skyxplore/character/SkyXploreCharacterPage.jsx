import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./page_localization.json";
import "./character_page.css";
import { useEffect, useState } from "react";
import NotificationService from "common/js/notification/NotificationService";
import Redirection from "../Redirection";
import ValidatedField from "common/js/validation/ValidatedField";
import NotificationKey from "common/js/notification/NotificationKey";
import Button from "common/component/input/Button";
import Header from "common/component/Header";
import PreLabeledInputField from "common/component/input/PreLabeledInputField";
import InputField from "common/component/input/InputField";
import ValidatedInputField from "common/component/input/ValidatedInputField";
import Footer from "common/component/Footer";
import { ToastContainer } from "react-toastify";
import Spinner from "common/component/Spinner";
import validate from "common/js/validation/Validator";
import "../skyxplore.css";
import { USER_DATA_GET_USERNAME } from "modules/etc/account/AccountEndpoints";
import { MODULES_PAGE } from "modules/etc/modules/ModulesEndpoints";
import { SKYXPLORE_MAIN_MENU_PAGE } from "../main_menu/SkyXploreMainMenuEndpoints";
import { SKYXPLORE_CREATE_OR_UPDATE_CHARACTER, SKYXPLORE_GET_CHARACTER_NAME, SKYXPLORE_PLATFORM_HAS_CHARACTER } from "./SkyXploreCharacterEndpoints";

const SkyXploreCharacterPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [hasCharacter, setHasCharacter] = useState(false);
    const [characterName, setCharacterName] = useState("");
    const [validationResult, setValidationResult] = useState({});
    const [displaySpinner, setDisplaySpinner] = useState(false);

    useEffect(() => checkRedirection(), []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => fetchCharacterExistence(), []);
    useEffect(() => prefillCharacterName(), [hasCharacter]);
    useEffect(() => runValidation(), [characterName]);

    const checkRedirection = () => {
        Redirection.forCharacter()
    }

    const fetchCharacterExistence = () => {
        const fetch = async () => {
            const response = await SKYXPLORE_PLATFORM_HAS_CHARACTER.createRequest()
                .send(setDisplaySpinner);
            setHasCharacter(response.value);
        }
        fetch();
    }

    const prefillCharacterName = () => {
        const fetchCharacterName = async () => {
            const response = await SKYXPLORE_GET_CHARACTER_NAME.createRequest()
                .send();
            setCharacterName(response.value);
        }
        const fetchUsername = async () => {
            const response = await USER_DATA_GET_USERNAME.createRequest()
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

        await SKYXPLORE_CREATE_OR_UPDATE_CHARACTER.createRequest(request)
            .send(setDisplaySpinner);

        sessionStorage.successCode = NotificationKey.SKYXPLORE_CHARACTER_SAVED;

        window.location.href = SKYXPLORE_MAIN_MENU_PAGE;
    }

    const backButton = <Button
        key={"back-button"}
        id={"back-button"}
        label={localizationHandler.get("back-button")}
        onclick={() => window.location.href = hasCharacter ? SKYXPLORE_MAIN_MENU_PAGE : MODULES_PAGE}
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
                                    <ValidatedInputField
                                        validationResultId="skyxplore-character-name-validation"
                                        validationResult={validationResult[ValidatedField.CHARACTER_NAME]}
                                        inputField={<InputField
                                            id="skyxplore-character-name"
                                            type="text"
                                            placeholder={localizationHandler.get("character-name")}
                                            onchangeCallback={setCharacterName}
                                            value={characterName}
                                        />}
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

                {displaySpinner && <Spinner />}
            </div>
        </div>
    );
}

export default SkyXploreCharacterPage;