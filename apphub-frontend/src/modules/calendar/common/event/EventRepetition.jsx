import LabelWrappedInputField from "../../../../common/component/input/LabelWrappedInputField";
import NumberInput from "../../../../common/component/input/NumberInput";
import PreLabeledInputField from "../../../../common/component/input/PreLabeledInputField";
import SelectInput, { MultiSelect, SelectOption } from "../../../../common/component/input/SelectInput";
import MapStream from "../../../../common/js/collection/MapStream";
import Stream from "../../../../common/js/collection/Stream";
import { DAYS_OF_WEEK } from "../../../../common/js/date/DayOfWeek";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { REPETITION_TYPE_DAYS_OF_MONTH, REPETITION_TYPE_DAYS_OF_WEEK, REPETITION_TYPE_EVERY_X_DAYS, REPETITION_TYPE_ONE_TIME, RepetitionType } from "../repetition_type/RepetitionType";
import localizationData from "./event_localization.json";
import repetitionTypeLocalizationData from "../repetition_type/repetition_type_localization.json";
import daysOfWeekLocalizationData from "../../../../common/js/date/day_of_week_localization.json";
import { throwException } from "../../../../common/js/Utils";

const EventRepetition = ({ repetitionType, setRepetitionType, repetitionData, setRepetitionData, repeatForDays, setRepeatForDays }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const repetitionTypeLocalizationHandler = new LocalizationHandler(repetitionTypeLocalizationData);
    const daysOfWeekLocalizationHandler = new LocalizationHandler(daysOfWeekLocalizationData);

    return (
        <span>
            <PreLabeledInputField
                label={localizationHandler.get("repetition-type")}
                input={<SelectInput
                    id="calendar-event-repetition-type"
                    value={repetitionType}
                    onchangeCallback={(newRepetitionType) => {
                        setRepetitionType(newRepetitionType);
                        setDefaultRepetitionData(newRepetitionType);
                    }}
                    options={getRepetitionTypes()}
                />}
            />

            {repetitionType === REPETITION_TYPE_EVERY_X_DAYS &&
                <LabelWrappedInputField
                    preLabel={localizationHandler.get("every-x-days-pre-label")}
                    postLabel={localizationHandler.get("every-x-days-post-label")}
                    inputField={<NumberInput
                        id="calendar-event-repetition-data"
                        value={repetitionData}
                        onchangeCallback={setRepetitionData}
                        min={1}
                    />}
                />
            }

            {repetitionType === REPETITION_TYPE_DAYS_OF_WEEK && typeof repetitionData === "object" &&
                <MultiSelect
                    id="calendar-event-repetition-data"
                    value={repetitionData}
                    onchangeCallback={setRepetitionData}
                    options={getDaysOfWeekOptions()}
                />
            }

            {repetitionType === REPETITION_TYPE_DAYS_OF_MONTH && typeof repetitionData === "object" &&
                <MultiSelect
                    id="calendar-event-repetition-data"
                    value={repetitionData}
                    onchangeCallback={setRepetitionData}
                    options={getDaysOfMonthOptions()}
                />
            }

            <LabelWrappedInputField
                preLabel={localizationHandler.get("repeat-for-pre-label")}
                postLabel={localizationHandler.get("repeat-for-post-label")}
                inputField={<NumberInput
                    id="calendar-event-repeat-for"
                    value={repeatForDays}
                    onchangeCallback={setRepeatForDays}
                    min={1}
                />}
            />
        </span>
    );

    function getRepetitionTypes() {
        return new MapStream(RepetitionType)
            .toListStream((k, v) => k)
            .map(r => new SelectOption(repetitionTypeLocalizationHandler.get(r), r))
            .toList();
    }

    function getDaysOfMonthOptions() {
        return new Stream(Array.from({ length: 31 }, (_, i) => i + 1))
            .map(day => new SelectOption(day, day))
            .toList();
    }

    function getDaysOfWeekOptions() {
        return new Stream(DAYS_OF_WEEK)
            .map(dayOfWeek => new SelectOption(daysOfWeekLocalizationHandler.get(dayOfWeek), dayOfWeek))
            .toList();
    }

    function setDefaultRepetitionData(newRepetitionType) {
        switch (newRepetitionType) {
            case REPETITION_TYPE_ONE_TIME:
                setRepetitionData("");
                break;
            case REPETITION_TYPE_EVERY_X_DAYS:
                setRepetitionData(1);
                break;
            case REPETITION_TYPE_DAYS_OF_WEEK:
            case REPETITION_TYPE_DAYS_OF_MONTH:
                setRepetitionData([]);
                break;
            default:
                throwException("IllegalArgument", "Unhandled repetitionType: " + repetitionType);
        }
    }
}

export default EventRepetition;