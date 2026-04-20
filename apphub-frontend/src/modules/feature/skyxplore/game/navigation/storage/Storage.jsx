import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./storage_settinggs_localization.json";
import "./storage.css";
import Button from "common/component/input/Button";
import StorageSettings from "./storage_settings/StorageSettings";

const Storage = ({ closePage, planetId, footer, setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div>
            <header id="skyxplore-game-storage-header">
                <h1>{localizationHandler.get("title")}</h1>

                <Button
                    id="skyxplore-game-storage-close-button"
                    className="skyxplore-game-window-close-button"
                    label="X"
                    onclick={closePage}
                />
            </header>

            <main id="skyxplore-game-storage">
                <StorageSettings
                    planetId={planetId}
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            </main>

            {footer}
        </div>
    );
}

export default Storage;