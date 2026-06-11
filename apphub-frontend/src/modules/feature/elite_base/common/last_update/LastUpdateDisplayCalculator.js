import Entry from "common/js/collection/Entry";
import Stream from "common/js/collection/Stream";
import LocalDateTime from "common/js/date/LocalDateTime";
import localizationData from "./last_update_display_calculator_localization.json";
import LocalizationHandler from "common/js/LocalizationHandler";

const localizationHandler = new LocalizationHandler(localizationData);

export const getLastUpdated = (lastUpdated) => {
    const luldt = LocalDateTime.fromLocalDateTime(lastUpdated);
    const deltaMs = LocalDateTime.now().getEpoch() - luldt.getEpoch();

    let seconds = Math.floor(deltaMs / 1000);
    let minutes = Math.floor(seconds / 60);
    let hours = Math.floor(minutes / 60);
    let days = Math.floor(hours / 24);

    hours = hours % 24;
    minutes = minutes % 60;
    seconds = seconds % 60;

    const arr = [
        new Entry("days", days),
        new Entry("hours", hours),
        new Entry("minutes", minutes),
        new Entry("seconds", seconds),
    ];

    const displayedArr = new Stream(arr)
        .filter(e => e.value > 0)
        .limit(2)
        .toList();

    if (displayedArr.length == 0) {
        return localizationHandler.get("just-now");
    } else if (displayedArr.length == 1) {
        return localizationHandler.get(displayedArr[0].key, { value: displayedArr[0].value }) + " " + localizationHandler.get("ago-suffix");
    } else {
        return new Stream([
            localizationHandler.get(displayedArr[0].key, { value: displayedArr[0].value }),
            localizationHandler.get("and"),
            localizationHandler.get(displayedArr[1].key, { value: displayedArr[1].value }),
            localizationHandler.get("ago-suffix"),
        ])
            .join(" ");
    }
}