import LocalizationHandler from "common/js/LocalizationHandler";
import constructionAreaLocalizationData from "../../../../../../../common/localization/construction_area_localization.json";

const ConstructionAreaDefaultHeader = ({  constructionArea }) => {
    const constructionAreaLocalizationHandler = new LocalizationHandler(constructionAreaLocalizationData);

    return (
        <div>
           {constructionAreaLocalizationHandler.get(constructionArea.dataId)}
        </div>
    );
}

export default ConstructionAreaDefaultHeader;