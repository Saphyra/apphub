import { SelectOption } from "../../../../../../common/component/input/SelectInput";
import MapStream from "../../../../../../common/js/collection/MapStream";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import localizationData from "./last_updated_localization.json";

const localizationHandler = new LocalizationHandler(localizationData);

const LastUpdateOptions = {
    PT10M: "10-minutes-ago",
    PT30M: "30-minutes-ago",
    PT1H: "1-hour-ago",
    PT4H: "4-hours-ago",
    PT12H: "12-hours-ago",
    P1D: "1-day-ago",
    P1W: "1-week-ago",
    P30D: "1-month-ago",
    P180D: "6-months-ago",
    P365D: "1-year-ago",
    P3650D: "unlimited",
};

export const getLastUpdateSelectOptions = () => {
    return new MapStream(LastUpdateOptions)
        .toList((key, value) => new SelectOption(localizationHandler.get(value), key));
}

export default LastUpdateOptions;