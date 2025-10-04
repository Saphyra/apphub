import Button from "../../../../common/component/input/Button";
import InputField from "../../../../common/component/input/InputField";
import Stream from "../../../../common/js/collection/Stream";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import Label from "./Label";
import localizationData from "./event_localization.json";
import { addAndSet, hasValue, isBlank, removeAndSet } from "../../../../common/js/Utils";
import { useState } from "react";
import useLoader from "../../../../common/hook/Loader";
import { CALENDAR_GET_LABELS } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import MapStream from "../../../../common/js/collection/MapStream";
import NotificationService from "../../../../common/js/notification/NotificationService";
import { MAX_LABEL_LENGTH } from "../../CalendarConstants";

const EventLabels = ({ existingLabels = [], setExistingLabels, setDisplaySpinner, newLabels, setNewLabels }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [availableLabels, setAvailableLabels] = useState(null);
    const [newLabel, setNewLabel] = useState("");

    useLoader({
        request: CALENDAR_GET_LABELS.createRequest(),
        mapper: v => setAvailableLabels(new Stream(v).toMap(i => i.labelId)),
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

        const el = new Stream(existingLabels)
            .toMap(
                labelId => availableLabels[labelId].label,
                labelId => <Label
                    key={labelId}
                    text={availableLabels[labelId].label}
                    callback={() => removeAndSet(existingLabels, l => l === labelId, setExistingLabels)}
                />);
        const nl = new Stream(newLabels)
            .toMap(
                label => label,
                label => <Label
                    key={label}
                    text={label}
                    callback={() => removeAndSet(newLabels, l => l === label, setNewLabels)}
                />);

        const merged = { ...el, ...nl };

        return new MapStream(merged)
            .sorted((a, b) => a.key.localeCompare(b.key))
            .toList();
    }

    function getAvailableLabels() {
        return new MapStream(availableLabels)
            .toListStream()
            .filter(label => existingLabels.indexOf(label.labelId) < 0)
            .sorted((a, b) => a.label.localeCompare(b.label))
            .map(label => <Label
                key={label.labelId}
                text={label.label}
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

        if (new MapStream(availableLabels).toListStream().anyMatch(label => label.label === newLabel)) {
            NotificationService.showError(localizationHandler.get("label-already-exists"));
            return;
        }

        if (new Stream(newLabels).anyMatch(label => label === newLabel)) {
            NotificationService.showError(localizationHandler.get("label-already-exists"));
            return;
        }

        addAndSet(newLabels, newLabel, setNewLabels);
        setNewLabel("");
    }
}

export default EventLabels;