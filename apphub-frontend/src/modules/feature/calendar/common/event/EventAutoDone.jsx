import PostLabeledInputField from "common/component/input/PostLabeledInputField";
import localizationData from "./event_localization.json";
import LocalizationHandler from "common/js/LocalizationHandler";
import InputField from "common/component/input/InputField";

const EventAutoDone = ({ value, setValue }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div>
            <PostLabeledInputField
                label={localizationHandler.get("auto-done")}
                input={<InputField
                    type="checkbox"
                    checked={value}
                    onchangeCallback={setValue}
                />
                }
            />
        </div>
    )
}

export default EventAutoDone;