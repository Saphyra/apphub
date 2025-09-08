import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import localizationData from "./repetition_type_localization.json";
import daysOfWeekLocalizationData from "../../../../common/js/date/day_of_week_localization.json";
import Stream from "../../../../common/js/collection/Stream";
import { DAYS_OF_WEEK } from "../../../../common/js/date/DayOfWeek";

const localizationHandler = new LocalizationHandler(localizationData);
const daysOfWeekLocalizationHandler = new LocalizationHandler(daysOfWeekLocalizationData);

export const REPETITION_TYPE_ONE_TIME = "ONE_TIME";
export const REPETITION_TYPE_EVERY_X_DAYS = "EVERY_X_DAYS";
export const REPETITION_TYPE_DAYS_OF_WEEK = "DAYS_OF_WEEK";
export const REPETITION_TYPE_DAYS_OF_MONTH = "DAYS_OF_MONTH";

export const RepetitionType = {};

RepetitionType[REPETITION_TYPE_ONE_TIME] = {
    display: () => null
};

RepetitionType[REPETITION_TYPE_EVERY_X_DAYS] = {
    display: repetitionData => localizationHandler.get("days", { days: repetitionData })
};

RepetitionType[REPETITION_TYPE_DAYS_OF_WEEK] = {
    display: repetitionData => {
        const days = new Stream(repetitionData)
            .sorted((a, b) => DAYS_OF_WEEK.indexOf(a) - DAYS_OF_WEEK.indexOf(b))
            .map(day => daysOfWeekLocalizationHandler.get(day))
            .join(", ");
        return localizationHandler.get("days-of-week", { days: days });
    }
};

RepetitionType[REPETITION_TYPE_DAYS_OF_MONTH] = {
    display: repetitionData => {
        const days = new Stream(repetitionData)
            .sorted((a, b) => a - b)
            .join(", ");
        return localizationHandler.get("days-of-month", { days: days });
    }
};