import { useParams } from "react-router";
import localizationData from "./calendar_edit_occurrence_page_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import sessionChecker from "../../../common/js/SessionChecker";
import { useEffect, useState } from "react";
import NotificationService from "../../../common/js/notification/NotificationService";
import Footer from "../../../common/component/Footer";
import { ToastContainer } from "react-toastify";
import Spinner from "../../../common/component/Spinner";
import Header from "../../../common/component/Header";
import Button from "../../../common/component/input/Button";
import { CALENDAR_EDIT_OCCURRENCE, CALENDAR_GET_OCCURRENCE, CALENDAR_PAGE } from "../../../common/js/dao/endpoints/CalendarEndpoints";
import useLoader from "../../../common/hook/Loader";
import { hasValue } from "../../../common/js/Utils";
import { useExtractAsync } from "../../../common/hook/UseEffectValidated";
import PreLabeledInputField from "../../../common/component/input/PreLabeledInputField";
import InputField from "../../../common/component/input/InputField";
import SelectInput, { SelectOption } from "../../../common/component/input/SelectInput";
import Stream from "../../../common/js/collection/Stream";
import { DONE, EXPIRED, PENDING, SNOOZED } from "../common/js/OccurrenceStatus";
import useRefresh from "../../../common/hook/Refresh";
import Textarea from "../../../common/component/input/Textarea";
import "./edit_occurrence.css";
import LabelWrappedInputField from "../../../common/component/input/LabelWrappedInputField";
import NumberInput from "../../../common/component/input/NumberInput";
import PostLabeledInputField from "../../../common/component/input/PostLabeledInputField";

/*
{
    "occurrenceId": "c84baac7-3e56-4ff7-98c6-3dfddb8a9f6d",
    "eventId": "67dd987b-ed6c-4365-9efa-9a1aee246ac2",
    "date": "2025-08-27",
    "time": "09:32:00",
    "status": "EXPIRED",
    "title": "First event",
    "content": "Well,\nThis\nIs\nThe\nSecond\nOne",
    "note": "",
    "remindMeBeforeDays": 1,
    "reminded": false
}
*/

const CalendarEditOccurrencePage = () => {
    const { occurrenceId } = useParams();

    const localizationHandler = new LocalizationHandler(localizationData);

    document.title = localizationHandler.get("title");
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [refreshCounter, refresh] = useRefresh();

    const [occurrence, setOccurrence] = useState(null);

    const [date, setDate] = useExtractAsync(o => o.date, occurrence);
    const [time, setTime] = useExtractAsync(o => o.time, occurrence);
    const [status, setStatus] = useExtractAsync(o => o.status == EXPIRED ? PENDING : o.status, occurrence, PENDING);
    const [note, setNote] = useExtractAsync(o => o.note, occurrence);
    const [remindMeBeforeDays, setRemindMeBeforeDays] = useExtractAsync(o => o.remindMeBeforeDays, occurrence);
    const [reminded, setReminded] = useExtractAsync(o => o.reminded, occurrence);

    useLoader(
        {
            request: CALENDAR_GET_OCCURRENCE.createRequest(null, { occurrenceId: occurrenceId }),
            mapper: setOccurrence,
            condition: () => hasValue(occurrenceId),
            listener: [occurrenceId, refreshCounter]
        }
    );

    return (
        <div id="calendar-edit-occurrence" className="main-page">
            {hasValue(occurrence) &&
                <Header label={localizationHandler.get("page-title", { eventTitle: occurrence.title, date: occurrence.date })} />
            }

            <main>
                <fieldset>
                    <legend>{localizationHandler.get("when")}</legend>

                    <PreLabeledInputField
                        label={localizationHandler.get("date")}
                        input={<InputField
                            id="calendar-edit-occurrence-date"
                            type={"date"}
                            value={date}
                            onchangeCallback={setDate}
                        />}
                    />

                    <span className="nowrap">
                        <PreLabeledInputField
                            label={localizationHandler.get("time")}
                            input={<InputField
                                id="calendar-edit-occurrence-time"
                                type={"time"}
                                value={time}
                                onchangeCallback={setTime}
                            />}
                        />

                        <Button
                            id="calendar-edit-occurrence-reset-time"
                            label={localizationHandler.get("reset-time")}
                            onclick={() => setTime(null)}
                        />
                    </span>
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("status")}</legend>

                    <SelectInput
                        id="calendar-edit-occurrence-status"
                        value={status}
                        onchangeCallback={setStatus}
                        options={getSelectOptions()}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("note")}</legend>

                    <Textarea
                        id="calendar-edit-occurrence-note"
                        placeholder={localizationHandler.get("note")}
                        value={note}
                        onchangeCallback={setNote}
                        onKeyUpCallback={e => {
                            e.target.style.height = "auto";
                            e.target.style.height = e.target.scrollHeight + 6 + "px";
                        }}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("settings")}</legend>

                    <div>
                        <LabelWrappedInputField
                            preLabel={localizationHandler.get("remind-me-before-days-pre-label")}
                            postLabel={localizationHandler.get("remind-me-before-days-post-label")}
                            inputField={<NumberInput
                                id="calendar-edit-occurrence-remind-me-before-days"
                                value={remindMeBeforeDays}
                                onchangeCallback={setRemindMeBeforeDays}
                                min={0}
                            />}
                        />
                    </div>

                    <PostLabeledInputField
                        label={localizationHandler.get("reminded")}
                        input={<InputField
                            id="calendar-edit-occurrence-reminded"
                            type="checkbox"
                            checked={reminded}
                            onchangeCallback={setReminded}
                        />}
                    />
                </fieldset>
            </main>

            <Footer
                leftButtons={[
                    <Button
                        key="reset"
                        id="calendar-edit-occurrence-reset-button"
                        label={localizationHandler.get("reset")}
                        onclick={() => refresh()}
                    />
                ]}
                centerButtons={[
                    <Button
                        key="save"
                        id="calendar-edit-occurrence-save-button"
                        label={localizationHandler.get("save")}
                        onclick={() => save()}
                    />
                ]}
                rightButtons={[
                    <Button
                        id="calendar-edit-occurrence-back-button"
                        key="back"
                        onclick={() => window.location.href = CALENDAR_PAGE}
                        label={localizationHandler.get("back")}
                    />
                ]}
            />

            <ToastContainer />

            {displaySpinner && <Spinner />}
        </div>
    );

    function getSelectOptions() {
        return new Stream([PENDING, DONE, SNOOZED])
            .map(status => new SelectOption(localizationHandler.get(status), status))
            .toList();
    }

    async function save() {
        //TODO validation

        const payload = {
            date: date,
            time: time,
            status: status,
            note: note,
            remindMeBeforeDays: remindMeBeforeDays,
            reminded: reminded
        };

        await CALENDAR_EDIT_OCCURRENCE.createRequest(payload, { occurrenceId: occurrenceId })
            .send(setDisplaySpinner);

        NotificationService.storeSuccessText(localizationHandler.get("saved"));
        window.location.href = CALENDAR_PAGE;
    }
}

export default CalendarEditOccurrencePage;