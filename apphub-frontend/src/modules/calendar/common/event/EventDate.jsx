import InputField from "../../../../common/component/input/InputField";
import PreLabeledInputField from "../../../../common/component/input/PreLabeledInputField";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { REPETITION_TYPE_ONE_TIME } from "../repetition_type/RepetitionType";
import localizationData from "./event_localization.json";

const EventDate = ({ repetitionType,  startDate, setStartDate, endDate, setEndDate }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <span>
            <PreLabeledInputField
                label={repetitionType === REPETITION_TYPE_ONE_TIME ? localizationHandler.get("date") : localizationHandler.get("start-date")}
                input={<InputField
                    id="calendar-event-start-date"
                    type={"date"}
                    value={startDate}
                    onchangeCallback={setStartDate}
                />}
            />

            {repetitionType !== REPETITION_TYPE_ONE_TIME &&
                <PreLabeledInputField
                    label={localizationHandler.get("end-date")}
                    input={<InputField
                        id="calendar-event-end-date"
                        type={"date"}
                        value={endDate}
                        onchangeCallback={setEndDate}
                    />}
                />
            }
        </span>
    );
}

export default EventDate;