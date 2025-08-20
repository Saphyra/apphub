import { useState } from "react";
import useLoader from "../../../../common/hook/Loader";
import { CALENDAR_GET_OCCURRENCE } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import { hasValue } from "../../../../common/js/Utils";
import LocalTime from "../../../../common/js/date/LocalTime";
import Textarea from "../../../../common/component/input/Textarea";

const OpenedOccurrence = ({ occurrenceId, localizationHandler }) => {
    const [occurrence, setOccurrence] = useState(null);

    useLoader(
        {
            request: CALENDAR_GET_OCCURRENCE.createRequest(null, { occurrenceId: occurrenceId }),
            mapper: setOccurrence,
            condition: () => hasValue(occurrenceId),
            listener: [occurrenceId]
        }
    );

    if (hasValue(occurrence)) {
        return (
            <div id="calendar-labels-opened-occurrence">
                <div id="calendar-labels-opened-occurrence-title">{occurrence.date}</div>

                <div id="calendar-labels-opened-occurrence-content">
                    {hasValue(occurrence.time) &&
                        <div id="calendar-labels-opened-occurrence-time">
                            <span>{localizationHandler.get("at")}</span>
                            <span>: </span>
                            <span>{LocalTime.parse(occurrence.time).formatWithoutSeconds()}</span>
                        </div>
                    }

                    <Textarea
                        id="calendar-labels-event-content"
                        value={occurrence.note}
                        disabled={true}
                        rows={Math.max(3, occurrence.note.split("\n").length)}
                        placeholder={localizationHandler.get("note")}
                    />

                    {occurrence.remindMeBeforeDays > 0 &&
                        <div id="calendar-labels-opened-occurrence-reminder">
                            {localizationHandler.get("reminder", { days: occurrence.remindMeBeforeDays })}
                        </div>
                    }

                    {occurrence.remindMeBeforeDays > 0 &&
                        <div id="calendar-labels-opened-occurrence-reminded">
                            {localizationHandler.get("reminder-confirmed", { value: localizationHandler.get(occurrence.reminded) })}
                        </div>
                    }
                </div>
            </div>
        );
    }
}

/*
{
    "remindMeBeforeDays": 1,
    "reminded": false
}
*/

export default OpenedOccurrence;