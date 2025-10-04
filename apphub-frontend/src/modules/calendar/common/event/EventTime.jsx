import Button from "../../../../common/component/input/Button";
import InputField from "../../../../common/component/input/InputField";
import PreLabeledInputField from "../../../../common/component/input/PreLabeledInputField";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import TestableTimeInput from "../input/TestableTimeInput";
import localizationData from "./event_localization.json";

const EventTime = ({ time, setTime }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <span className="nowrap">
            <PreLabeledInputField
                label={localizationHandler.get("time")}
                input={<TestableTimeInput
                    id="calendar-event-time"
                    time={time}
                    setTime={setTime}
                />}
            />

            <Button
                id="calendar-event-reset-time"
                label={localizationHandler.get("reset-time")}
                onclick={() => setTime(null)}
            />
        </span>
    );
};

export default EventTime;