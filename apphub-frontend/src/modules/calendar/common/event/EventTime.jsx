import Button from "../../../../common/component/input/Button";
import InputField from "../../../../common/component/input/InputField";
import PreLabeledInputField from "../../../../common/component/input/PreLabeledInputField";
import Constants from "../../../../common/js/Constants";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { isTrue } from "../../../../common/js/Utils";
import localizationData from "./event_localization.json";

const EventTime = ({ time, setTime }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (isTrue(sessionStorage[Constants.STORAGE_KEY_TEST_MODE])) {
        return (
            <span className="nowrap">
                <PreLabeledInputField
                    label={localizationHandler.get("time")}
                    input={<InputField
                        id="calendar-event-time"
                        value={time}
                        onchangeCallback={setTime}
                    />}
                />

                <Button
                    id="calendar-event-reset-time"
                    label={localizationHandler.get("reset-time")}
                    onclick={() => setTime(null)}
                />
            </span>
        );
    }

    return (
        <span className="nowrap">
            <PreLabeledInputField
                label={localizationHandler.get("time")}
                input={<InputField
                    id="calendar-event-time"
                    type={"time"}
                    value={time}
                    onchangeCallback={setTime}
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