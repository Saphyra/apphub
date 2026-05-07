import React from "react";
import localizationData from "./landing_pad_selector_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import LandingPad from "./LandingPad";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import SelectInput from "../../../../../../common/component/input/SelectInput";

const LandingPadSelector = ({ landingPad, setLandingPad }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div>
            <PreLabeledInputField
                label={localizationHandler.get("min-landing-pad") + ":"}
                input={<SelectInput
                    value={landingPad}
                    onchangeCallback={setLandingPad}
                    options={LandingPad.getOptions()}
                />}
            />
        </div>
    );
}

export default LandingPadSelector;