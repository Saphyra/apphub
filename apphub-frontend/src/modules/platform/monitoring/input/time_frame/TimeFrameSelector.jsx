import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import Stream from "../../../../../common/js/collection/Stream";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import timeFrameLocalizationData from "./time_frame_localization.json";
import TimeFrame from "./TimeFrame";

const TimeFrameSelector = ({ localizationHandler, timeFrame, setTimeFrame }) => {
    const timeFrameLocalizationHandler = new LocalizationHandler(timeFrameLocalizationData);

    return <PreLabeledInputField
        label={localizationHandler.get("time-frame")}
        input={<SelectInput
            id="monitoring-time-frame"
            value={timeFrame}
            onchangeCallback={setTimeFrame}
            options={getOptions()}
        />}
    />



    function getOptions() {
        return new Stream(Object.values(TimeFrame))
            .map(timeFrame => new SelectOption(timeFrameLocalizationHandler.get(timeFrame), timeFrame))
            .toList();
    }
}

export default TimeFrameSelector;