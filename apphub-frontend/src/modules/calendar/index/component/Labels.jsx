import { useState } from "react";
import useLoader from "../../../../common/hook/Loader";
import { CALENDAR_GET_LABELS, CALENDAR_LABELS_PAGE } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import Button from "../../../../common/component/input/Button";
import Stream from "../../../../common/js/collection/Stream";

const Labels = ({ activeLabel, setActiveLabel }) => {
    const [labels, setLabels] = useState([]);

    useLoader({ request: CALENDAR_GET_LABELS.createRequest(), mapper: setLabels });

    return (
        <div id="calendar-labels">
            <Button
                id="modify-labels-button"
                onclick={() => window.location.href = CALENDAR_LABELS_PAGE}
                label={"X"}
            />

            <Button
                id="calendar-deselect-label-button"
                onclick={() => setActiveLabel(null)}
                label={"X"}
            />

            {getLabels()}
        </div>
    );

    function getLabels() {
        return new Stream(labels)
            .map(label =>
                <Button
                    key={label.labelId}
                    className={"calendar-label" + (activeLabel === label.labelId ? " active" : "")}
                    onclick={() => setActiveLabel(label.labelId)}
                    label={label.label}
                />
            )
            .toList();
    }
}

export default Labels;