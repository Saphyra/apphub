import Textarea from "../../../../common/component/input/Textarea";
import Stream from "../../../../common/js/collection/Stream";
import LocalTime from "../../../../common/js/date/LocalTime";
import { generateRandomId, hasValue, isBlank } from "../../../../common/js/Utils";

function getContent(localizationHandler, occurrence, labels) {
    return (
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
    );
}

export default getContent;