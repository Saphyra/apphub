import localizationData from "./last_updated_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import SelectInput from "../../../../../../common/component/input/SelectInput";
import { getLastUpdateSelectOptions } from "./LastUpdateOptions";

const LastUpdateSelector = ({ lastUpdate, setLastUpdate }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div>
            <PreLabeledInputField
                label={localizationHandler.get("time-since-last-updated") + ":"}
                input={<SelectInput
                    value={lastUpdate}
                    onchangeCallback={setLastUpdate}
                    options={getLastUpdateSelectOptions()}
                />
                }
            />
        </div>
    );
}

export default LastUpdateSelector;