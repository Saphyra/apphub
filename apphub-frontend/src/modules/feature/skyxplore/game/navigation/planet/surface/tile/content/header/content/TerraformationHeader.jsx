import LocalizationHandler from "common/js/LocalizationHandler";
import surfaceLocalizationData from "../../../../../../../common/localization/surface_localization.json";

const TerraformationHeader = ({ terraformation}) => {
    const surfaceLocalizationHandler = new LocalizationHandler(surfaceLocalizationData);

    return (
        <div>
            <span>{"=> "}</span>
            <span className="skyxplore-planet-surface-header-terraformation-target">{surfaceLocalizationHandler.get(terraformation.data)}</span>
        </div>
    )
}

export default TerraformationHeader;