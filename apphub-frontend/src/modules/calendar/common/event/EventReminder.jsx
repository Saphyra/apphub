import LabelWrappedInputField from "../../../../common/component/input/LabelWrappedInputField";
import NumberInput from "../../../../common/component/input/NumberInput";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import localizationData from "./event_localization.json";

const EventReminder = ({ remindMeBeforeDays, setRemindMeBeforeDays }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <LabelWrappedInputField
            preLabel={localizationHandler.get("remind-me-before-days-pre-label")}
            postLabel={localizationHandler.get("remind-me-before-days-post-label")}
            inputField={<NumberInput
                id="calendar-event-remind-me-before-days"
                value={remindMeBeforeDays}
                onchangeCallback={setRemindMeBeforeDays}
                min={0}
            />}
        />
    );
}

export default EventReminder;