import LocalizationHandler from "common/js/LocalizationHandler";
import constructionAreaLocalizationData from "../../../../../../../common/localization/construction_area_localization.json";

const ConstructionAreaConstructionHeader = ({ constructionArea }) => {
    const localizationHandler = new LocalizationHandler(constructionAreaLocalizationData);
    
    return (
        <div>
            {localizationHandler.get(constructionArea.dataId)}
        </div>
    );
}

export default ConstructionAreaConstructionHeader;