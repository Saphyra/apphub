import useQueryParams from "common/hook/UseQueryParams";
import LocalizationHandler from "common/js/LocalizationHandler";
import { useParams } from "react-router";
import localizationData from "./calendar_edit_occurrence_page_localization.json";
import "./edit_occurrence.css";
import { useEffect, useState } from "react";
import NotificationService from "common/js/notification/NotificationService";
import useRefresh from "common/hook/Refresh";
import { hasValue } from "common/js/Utils";
import { useExtractAsync } from "common/hook/UseEffectValidated";
import LocalTime from "common/js/date/LocalTime";
import LocalDate from "common/js/date/LocalDate";
import { DONE, EXPIRED, PENDING, SNOOZED } from "../common/occurrence/OccurrenceStatus";
import useLoader from "common/hook/Loader";
import Header from "common/component/Header";
import PreLabeledInputField from "common/component/input/PreLabeledInputField";
import TestableDateInput from "../common/input/TestableDateInput";
import TestableTimeInput from "../common/input/TestableTimeInput";
import Button from "common/component/input/Button";
import SelectInput, { SelectOption } from "common/component/input/SelectInput";
import Textarea from "common/component/input/Textarea";
import LabelWrappedInputField from "common/component/input/LabelWrappedInputField";
import NumberInput from "common/component/input/NumberInput";
import PostLabeledInputField from "common/component/input/PostLabeledInputField";
import InputField from "common/component/input/InputField";
import Footer from "common/component/Footer";
import confirmOccurrenceDeletion from "../common/delete_occurrence/DeleteOccurrence";
import save from "./EditOccurrence";
import Optional from "common/js/collection/Optional";
import { ToastContainer } from "react-toastify";
import ConfirmationDialog from "common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "common/component/Spinner";
import Stream from "common/js/collection/Stream";
import { CALENDAR_GET_OCCURRENCE, CALENDAR_PAGE } from "../CalendarEndpoints";

const CalendarEditOccurrencePage = () => {
    const { eventId, occurrenceId } = useParams();
    const queryParams = useQueryParams();

    const localizationHandler = new LocalizationHandler(localizationData);

    document.title = localizationHandler.get("title");
    useEffect(() => NotificationService.displayStoredMessages(), []);
    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [refreshCounter, refresh] = useRefresh();
    const [backUrl, setBackUrl] = useState(hasValue(queryParams.backUrl) ? queryParams.backUrl : CALENDAR_PAGE);

    const [occurrence, setOccurrence] = useState(null);

    const [date, setDate] = useExtractAsync(o => LocalDate.parse(o.date), occurrence, null);
    const [time, setTime] = useExtractAsync(
        o => LocalTime.parse(o.time),
        occurrence,
        null,
        e => hasValue(e) && hasValue(e.time)
    );
    const [status, setStatus] = useExtractAsync(o => o.status == EXPIRED ? PENDING : o.status, occurrence, PENDING);
    const [note, setNote] = useExtractAsync(o => o.note, occurrence, "");
    const [remindMeBeforeDays, setRemindMeBeforeDays] = useExtractAsync(o => o.remindMeBeforeDays, occurrence, 0);
    const [reminded, setReminded] = useExtractAsync(o => o.reminded, occurrence, false);

    useLoader(
        {
            request: CALENDAR_GET_OCCURRENCE.createRequest(null, {eventId: eventId, occurrenceId: occurrenceId }),
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
                        input={<TestableDateInput
                            id="calendar-edit-occurrence-date"
                            date={date}
                            setDate={setDate}
                        />}
                    />

                    <span className="nowrap">
                        <PreLabeledInputField
                            label={localizationHandler.get("time")}
                            input={<TestableTimeInput
                                id="calendar-edit-occurrence-time"
                                time={time}
                                setTime={setTime}
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
                    />,
                    <Button
                        key="delete"
                        id="calendar-edit-occurrence-delete-button"
                        label={localizationHandler.get("delete")}
                        onclick={() => confirmOccurrenceDeletion(
                            setConfirmationDialogData,
                            occurrence.eventId,
                            occurrenceId,
                            occurrence.title,
                            occurrence.date,
                            setDisplaySpinner,
                            () => { },
                            () => window.location.href = CALENDAR_PAGE
                        )}
                    />
                ]}
                centerButtons={[
                    <Button
                        key="save"
                        id="calendar-edit-occurrence-save-button"
                        label={localizationHandler.get("save")}
                        onclick={() => save({
                            localizationHandler: localizationHandler,
                            occurrenceId: occurrenceId,
                            eventId: eventId,
                            date: new Optional(date).map(d => d.toString()).orElse(null),
                            time: new Optional(time).map(d => d.formatWithoutSeconds()).orElse(null),
                            status: status,
                            note: note,
                            remindMeBeforeDays: remindMeBeforeDays,
                            reminded: reminded,
                            setDisplaySpinner: setDisplaySpinner,
                            setConfirmationDialogData: setConfirmationDialogData,
                            refresh: refresh,
                            backUrl: backUrl
                        })}
                    />
                ]}
                rightButtons={[
                    <Button
                        id="calendar-edit-occurrence-back-button"
                        key="back"
                        onclick={() => window.location.href = backUrl}
                        label={localizationHandler.get("back")}
                    />
                ]}
            />

            <ToastContainer />

            {confirmationDialogData &&
                <ConfirmationDialog
                    id={confirmationDialogData.id}
                    title={confirmationDialogData.title}
                    content={confirmationDialogData.content}
                    choices={confirmationDialogData.choices}
                />
            }

            {displaySpinner && <Spinner />}
        </div>
    );

    function getSelectOptions() {
        return new Stream([PENDING, DONE, SNOOZED])
            .map(status => new SelectOption(localizationHandler.get(status), status))
            .toList();
    }
}

export default CalendarEditOccurrencePage;