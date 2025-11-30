import { useState } from "react";
import useLoader from "../../../../../common/hook/Loader";
import { CALENDAR_GET_LABELS_OF_EVENT, CALENDAR_GET_OCCURRENCE } from "../../../../../common/js/dao/endpoints/CalendarEndpoints";
import ErrorHandler from "../../../../../common/js/dao/ErrorHandler";
import { ResponseStatus } from "../../../../../common/js/dao/dao";
import { generateRandomId, hasValue, isBlank } from "../../../../../common/js/Utils";
import LocalTime from "../../../../../common/js/date/LocalTime";
import Textarea from "../../../../../common/component/input/Textarea";
import Stream from "../../../../../common/js/collection/Stream";
import getChoices from "./SelectedOccurrenceChoices";
import Button from "../../../../../common/component/input/Button";

const CelectedOccurrenceContent = ({
    occurrenceId,
    setDisplaySpinner,
    setSelectedOccurrence,
    localizationHandler,
    refresh,
    setConfirmationDialogData
}) => {
    const [occurrence, setOccurrence] = useState(null);
    const [labels, setLabels] = useState([]);

    useLoader({
        request: CALENDAR_GET_OCCURRENCE.createRequest(null, { occurrenceId: occurrenceId }),
        mapper: setOccurrence,
        listener: [occurrenceId],
        setDisplaySpinner: setDisplaySpinner,
        errorHandler: new ErrorHandler(
            (response) => response.status == ResponseStatus.NOT_FOUND,
            () => setSelectedOccurrence(null))
    });

    useLoader({
        request: () => CALENDAR_GET_LABELS_OF_EVENT.createRequest(null, { eventId: occurrence.eventId }),
        mapper: setLabels,
        listener: [occurrence],
        setDisplaySpinner: setDisplaySpinner,
        condition: () => hasValue(occurrence)
    });

    if (hasValue(occurrence)) {
        return (
            <div id="calendar-selected-occurrence">
                <div id="calendar-selected-occurrence-title" className="nowrap">{occurrence.title}</div>

                <Button
                    id="calendar-selected-occurrence-close-button"
                    label="X"
                    onclick={() => setSelectedOccurrence(null)}
                />

                <div id="calendar-selected-occurrence-content">
                    <div>{occurrence.date}</div>

                    {hasValue(occurrence.time) && <div>{LocalTime.parse(occurrence.time).formatWithoutSeconds()}</div>}

                    {!isBlank(occurrence.content) &&
                        <div>
                            <Textarea
                                id="calendar-selected-occurrence-content"
                                value={occurrence.content}
                                disabled={true}
                                rows={Math.max(3, occurrence.content.split("\n").length)}
                            />
                        </div>
                    }

                    {!isBlank(occurrence.note) &&
                        <div>
                            <Textarea
                                id="calendar-selected-occurrence-note"
                                value={occurrence.note}
                                disabled={true}
                                rows={Math.max(3, occurrence.note.split("\n").length)}
                            />
                        </div>
                    }

                    {labels.length > 0 &&
                        <div id="calendar-selected-occurrence-labels">
                            <span>{localizationHandler.get("labels")}</span>
                            <span>: </span>
                            {
                                new Stream(labels)
                                    .map(label => <span key={label.labelId}>{label.label}</span>)
                                    .joinToArray(() => <span key={generateRandomId()}>, </span>)
                            }
                        </div>
                    }
                </div>

                <div id="calendar-selected-occurrence-choices">
                    {getChoices({
                        occurrence: occurrence,
                        localizationHandler: localizationHandler,
                        setOccurrence: setOccurrence,
                        setDisplaySpinner: setDisplaySpinner,
                        setSelectedOccurrence: setSelectedOccurrence,
                        refresh: refresh,
                        setConfirmationDialogData: setConfirmationDialogData
                    })}
                </div>
            </div>
        );
    };
}

export default CelectedOccurrenceContent;
