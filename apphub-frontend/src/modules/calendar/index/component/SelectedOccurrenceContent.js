import Textarea from "../../../../common/component/input/Textarea";
import LocalTime from "../../../../common/js/date/LocalTime";
import { hasValue, isBlank } from "../../../../common/js/Utils";

function getContent(occurrence) {
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
        </div>
    );
}

export default getContent;