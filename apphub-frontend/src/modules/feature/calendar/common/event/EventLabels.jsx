import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./event_localization.json";
import { useState } from "react";
import useLoader from "common/hook/Loader";
import Stream from "common/js/collection/Stream";
import InputField from "common/component/input/InputField";
import Button from "common/component/input/Button";
import { addAndSet, generateRandomId, hasValue, isBlank, removeAndSet } from "common/js/Utils";
import Label from "./Label";
import MapStream from "common/js/collection/MapStream";
import NotificationService from "common/js/notification/NotificationService";
import { MAX_LABEL_LENGTH } from "../../CalendarConstants";
import { CALENDAR_GET_LABELS } from "../../CalendarEndpoints";

const EventLabels = ({ existingLabels = [], setExistingLabels, setDisplaySpinner, newLabels, setNewLabels }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [availableLabels, setAvailableLabels] = useState(null);
    const [newLabel, setNewLabel] = useState("");

    useLoader({
        request: CALENDAR_GET_LABELS.createRequest(),
        mapper: setAvailableLabels,
        setDisplaySpinner: setDisplaySpinner
    });

    return (
        <span>
            <div id="calendar-event-labels">{localizationHandler.get("labels-of-event")}: {getLabels()}</div>
            <div id="calendar-event-available-labels">{localizationHandler.get("available-labels")}: {getAvailableLabels()}</div>
            <div>
                <InputField
                    id="calendar-event-new-label"
                    value={newLabel}
                    onchangeCallback={setNewLabel}
                    placeholder={localizationHandler.get("new-label")}
                />

                <Button
                    id="calendar-event-new-label-button"
                    label={localizationHandler.get("add")}
                    onclick={addNewLabel}
                />
            </div>
        </span>
    );

    function getLabels() {
        if (!hasValue(availableLabels)) {
            return [];
        }

        return new Stream(existingLabels.concat(newLabels))
            .sorted((a, b) => a.label.localeCompare(b.label))
            .map(label => <Label
                key={label.labelId}
                text={label.label}
                callback={() => {
                    removeAndSet(existingLabels, l => l.labelId === label.labelId, setExistingLabels);
                    removeAndSet(newLabels, l => l.labelId === label.labelId, setNewLabels);
                }}
            />)
            .toList();
    }

    function getAvailableLabels() {
        return new Stream(availableLabels)
            .filter(label => new Stream(existingLabels).noneMatch(l => l.labelId === label.labelId))
            .sorted((a, b) => a.label.localeCompare(b.label))
            .map(label => <Label
                key={label.labelId}
                text={label.label}
                shared={label.shared}
                callback={() => addAndSet(existingLabels, label.labelId, setExistingLabels)}
            />)
            .toList();
    }

    function addNewLabel() {
        if (isBlank(newLabel)) {
            NotificationService.showError(localizationHandler.get("new-label-too-short"));
            return;
        }

        if (newLabel.length > MAX_LABEL_LENGTH) {
            NotificationService.showError(localizationHandler.get("new-label-too-long"));
            return;
        }

        addAndSet(newLabels, { labelId: generateRandomId(), label: newLabel }, setNewLabels);
        setNewLabel("");
    }
}

export default EventLabels;