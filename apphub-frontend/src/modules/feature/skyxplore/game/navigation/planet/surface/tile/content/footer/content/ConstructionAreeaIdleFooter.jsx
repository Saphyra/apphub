import Button from "common/component/input/Button";
import constructionAreaLocalizationData from "../../../../../../../common/localization/construction_area_localization.json";
import LocalizationHandler from "common/js/LocalizationHandler";
import confirmDeconstructConstructionArea from "./DeconstructConstructionAreaService";
import NavigationHistoryItem from "modules/feature/skyxplore/game/navigation/NavigationHistoryItem";
import PageName from "modules/feature/skyxplore/game/navigation/PageName";

const ConstructionAreeaIdleFooter = ({ localizationHandler, constructionArea, setConfirmationDialogData, openPage, setDisplaySpinner }) => {
    const constructionAreaLocalizationHandler = new LocalizationHandler(constructionAreaLocalizationData);

    return (
        <div>
            <Button
                className="skyxplore-game-planet-surface-construction-area-deconstruct-button"
                label="X"
                title={localizationHandler.get("deconstruct")}
                onclick={() => confirmDeconstructConstructionArea(
                    localizationHandler,
                    constructionAreaLocalizationHandler,
                    constructionArea,
                    setConfirmationDialogData,
                    setDisplaySpinner
                )}
            />
            <Button
                className="skyxplore-game-planet-surface-construction-area-open-button"
                label=""
                onclick={() => openPage(new NavigationHistoryItem(PageName.CONSTRUCTION_AREA, { constructionArea: constructionArea }))}
            />
        </div>
    );
}

export default ConstructionAreeaIdleFooter;