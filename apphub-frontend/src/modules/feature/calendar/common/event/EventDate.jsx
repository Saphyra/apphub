import PreLabeledInputField from "common/component/input/PreLabeledInputField";
import TestableDateInput from "../input/TestableDateInput";
import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./event_localization.json";
import { REPETITION_TYPE_ONE_TIME } from "../repetition_type/RepetitionType";

const EventDate = ({ repetitionType, startDate, setStartDate, endDate, setEndDate }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <span>
            <PreLabeledInputField
                label={repetitionType === REPETITION_TYPE_ONE_TIME ? localizationHandler.get("date") : localizationHandler.get("start-date")}
                input={<TestableDateInput
                    id="calendar-event-start-date"
                    date={startDate}
                    setDate={setStartDate}
                />}
            />

            {repetitionType !== REPETITION_TYPE_ONE_TIME &&
                <PreLabeledInputField
                    label={localizationHandler.get("end-date")}
                    input={<TestableDateInput
                        id="calendar-event-end-date"
                        date={endDate}
                        setDate={setEndDate}
                    />}
                />
            }
        </span>
    );
}

export default EventDate;