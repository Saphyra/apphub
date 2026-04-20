import LocalizationHandler from "common/js/LocalizationHandler";
import buildingModuleLocalizationData from "../../../../../common/localization/building_module_localization.json";

const ConstructBuildingModuleHeader = ({ localizationHandler, queueItem }) => {
    const buildingModuleLocalizationHandler = new LocalizationHandler(buildingModuleLocalizationData);

    return (
        <div className="skyxplore-game-planet-queue-item-header">
            <span>{localizationHandler.get("construct")}</span>
            <span>: </span>
            <span>{buildingModuleLocalizationHandler.get(queueItem.data.dataId)}</span>
        </div>
    );
}

export default ConstructBuildingModuleHeader;