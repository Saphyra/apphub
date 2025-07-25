import LocalDate, { DAYS_IN_WEEK } from "../../../../common/js/date/LocalDate";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import dateLocalizationData from "../localization/date_localization.json";
import { MONDAY, SUNDAY } from "../../../../common/js/date/DayOfWeek";

export const WEEK = "WEEK";
export const SURROUNDING_WEEKS = "SURROUNDING_WEEKS";
export const MONTH = "MONTH";

export const View = {
}

const localizationHandler = new LocalizationHandler(dateLocalizationData);

View[WEEK] = {
    back: (referenceDate, setReferenceDate) => {
        setReferenceDate(referenceDate.minusDays(DAYS_IN_WEEK));
    },
    forward: (referenceDate, setReferenceDate) => {
        setReferenceDate(referenceDate.plusDays(DAYS_IN_WEEK));
    },
    format: (referenceDate) => {
        return localizationHandler.get("view-format-week", { year: referenceDate.getYear(), week: referenceDate.getWeekOfYear() });
    },
    startDate: (referenceDate) => {
        let result = referenceDate;
        while (result.getDayOfWeek() !== MONDAY) {
            result = result.minusDays(1);
        }

        return result;
    },
    endDate: (referenceDate) => {
        let result = referenceDate;
        while (result.getDayOfWeek() !== SUNDAY) {
            result = result.plusDays(1);
        }

        return result;
    }
};
View[SURROUNDING_WEEKS] = {
    back: (referenceDate, setReferenceDate) => {
        setReferenceDate(referenceDate.minusDays(DAYS_IN_WEEK));
    },
    forward: (referenceDate, setReferenceDate) => {
        setReferenceDate(referenceDate.plusDays(DAYS_IN_WEEK));
    },
    format: (referenceDate) => {
        return localizationHandler.get(
            "view-format-surrounding-weeks",
            {
                year: referenceDate.getYear(),
                startWeek: referenceDate.minusDays(DAYS_IN_WEEK).getWeekOfYear(),
                endWeek: referenceDate.plusDays(DAYS_IN_WEEK).getWeekOfYear(),
            }
        );
    },
    startDate: (referenceDate) => {
        let result = referenceDate.minusDays(DAYS_IN_WEEK);
        while (result.getDayOfWeek() !== MONDAY) {
            result = result.minusDays(1);
        }

        return result;
    },
    endDate: (referenceDate) => {
        let result = referenceDate.plusDays(DAYS_IN_WEEK);
        while (result.getDayOfWeek() !== SUNDAY) {
            result = result.plusDays(1);
        }

        return result;
    }
};
View[MONTH] = {
    back: (referenceDate, setReferenceDate) => {
        setReferenceDate(referenceDate.minusMonths(1));
    },
    forward: (referenceDate, setReferenceDate) => {
        setReferenceDate(referenceDate.plusMonths(1));
    },
    format: (referenceDate) => {
        return localizationHandler.get(
            "view-format-month",
            {
                year: referenceDate.getYear(),
                month: localizationHandler.get(referenceDate.getMonth())
            }
        );
    },
    startDate: (referenceDate) => {
        let result = LocalDate.of(referenceDate.getYear(), referenceDate.getMonth() - 1, 1);
        while (result.getDayOfWeek() !== MONDAY) {
            result = result.minusDays(1);
        }

        return result;
    },
    endDate: (referenceDate) => {
        let result = LocalDate.of(referenceDate.getYear(), referenceDate.getMonth(), 1).minusDays(1);
        while (result.getDayOfWeek() !== SUNDAY) {
            result = result.plusDays(1);
        }

        return result;
    }
};