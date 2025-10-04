import { useParams } from "react-router";
import localizationData from "./calendar_edit_event_page_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import { useEffect, useState } from "react";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import useRefresh from "../../../common/hook/Refresh";
import { CALENDAR_GET_EVENT, CALENDAR_PAGE } from "../../../common/js/dao/endpoints/CalendarEndpoints";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import useLoader from "../../../common/hook/Loader";
import { hasValue, nullIfEmpty } from "../../../common/js/Utils";
import { ToastContainer } from "react-toastify";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "../../../common/component/Spinner";
import { useExtractAsync } from "../../../common/hook/UseEffectValidated";
import { REPETITION_TYPE_ONE_TIME } from "../common/repetition_type/RepetitionType";
import confirmEventDeletion from "../common/delete_event/DeleteEvent";
import EventDate from "../common/event/EventDate";
import EventTime from "../common/event/EventTime";
import EventContent from "../common/event/EventContent";
import EventRepetition from "../common/event/EventRepetition";
import EventReminder from "../common/event/EventReminder";
import EventLabels from "../common/event/EventLabels";
import ConfirmationDialogData from "../../../common/component/confirmation_dialog/ConfirmationDialogData";
import saveEvent from "./saveEvent";
import LocalDate from "../../../common/js/date/LocalDate";
import Optional from "../../../common/js/collection/Optional";
import LocalTime from "../../../common/js/date/LocalTime";

const CalendarEditEventPage = () => {
    const { eventId } = useParams();

    const localizationHandler = new LocalizationHandler(localizationData);

    document.title = localizationHandler.get("title");
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [refreshCounter, refresh] = useRefresh();

    const [event, setEvent] = useState(null);
    const [newLabels, setNewLabels] = useState([]);

    const [title, setTitle] = useExtractAsync(o => o.title, event, "");
    const [content, setContent] = useExtractAsync(o => o.content, event, "");
    const [startDate, setStartDate] = useExtractAsync(o => LocalDate.parse(o.startDate), event, null);
    const [endDate, setEndDate] = useExtractAsync(
        o => LocalDate.parse(o.endDate),
        event,
        null,
        e => hasValue(e) && hasValue(e.endDate)
    );
    const [time, setTime] = useExtractAsync(
        o => LocalTime.parse(o.time),
        event,
        null,
        e => hasValue(e) && hasValue(e.time)
    );
    const [repetitionType, setRepetitionType] = useExtractAsync(o => o.repetitionType, event, REPETITION_TYPE_ONE_TIME);
    const [repetitionData, setRepetitionData] = useExtractAsync(o => o.repetitionData, event);
    const [repeatForDays, setRepeatForDays] = useExtractAsync(o => o.repeatForDays, event, 1);
    const [remindMeBeforeDays, setRemindMeBeforeDays] = useExtractAsync(o => o.remindMeBeforeDays, event, 0);
    const [existingLabels, setExistingLabels] = useExtractAsync(o => o.labels, event, []);

    useLoader(
        {
            request: CALENDAR_GET_EVENT.createRequest(null, { eventId: eventId }),
            mapper: setEvent,
            condition: () => hasValue(eventId),
            listener: [eventId, refreshCounter]
        }
    );

    return (
        <div id="calendar-edit-event" className="main-page">
            {hasValue(event) &&
                <Header label={localizationHandler.get("page-title", { title: event.title })} />
            }

            <main className="calendar-event">
                <fieldset>
                    <legend>{localizationHandler.get("when")}</legend>

                    <EventDate
                        repetitionType={repetitionType}
                        startDate={startDate}
                        setStartDate={setStartDate}
                        endDate={endDate}
                        setEndDate={setEndDate}
                    />

                    <EventTime
                        time={time}
                        setTime={setTime}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("what")}</legend>

                    <EventContent
                        title={title}
                        setTitle={setTitle}
                        content={content}
                        setContent={setContent}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("repetition")}</legend>

                    <EventRepetition
                        repetitionType={repetitionType}
                        setRepetitionType={setRepetitionType}
                        repetitionData={repetitionData}
                        setRepetitionData={setRepetitionData}
                        repeatForDays={repeatForDays}
                        setRepeatForDays={setRepeatForDays}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("reminder")}</legend>

                    <EventReminder
                        remindMeBeforeDays={remindMeBeforeDays}
                        setRemindMeBeforeDays={setRemindMeBeforeDays}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("labels")}</legend>

                    <EventLabels
                        existingLabels={existingLabels}
                        setExistingLabels={setExistingLabels}
                        setDisplaySpinner={setDisplaySpinner}
                        newLabels={newLabels}
                        setNewLabels={setNewLabels}
                    />
                </fieldset>
            </main>

            <Footer
                leftButtons={[
                    <Button
                        key="reset"
                        id="calendar-edit-event-reset-button"
                        label={localizationHandler.get("reset")}
                        onclick={() => refresh()}
                    />,
                    <Button
                        key="delete"
                        id="calendar-edit-event-delete-button"
                        label={localizationHandler.get("delete")}
                        onclick={() => confirmEventDeletion(
                            setConfirmationDialogData,
                            eventId,
                            event.title,
                            setDisplaySpinner,
                            () => { },
                            () => window.location.href = CALENDAR_PAGE
                        )}
                    />
                ]}
                centerButtons={[
                    <Button
                        key="save"
                        id="calendar-edit-event-save-button"
                        label={localizationHandler.get("save")}
                        onclick={() => confirmSave()}
                    />
                ]}
                rightButtons={[
                    <Button
                        id="calendar-edit-event-back-button"
                        key="back"
                        onclick={() => window.location.href = CALENDAR_PAGE}
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

    function confirmSave() {
        setConfirmationDialogData(new ConfirmationDialogData(
            "calendar-edit-event-confirm-save",
            localizationHandler.get("edit-event-confirmation-title"),
            localizationHandler.get("edit-event-confirmation-detail"),
            [
                <Button
                    key="save"
                    id="calendar-edit-event-confirm-save-button"
                    label={localizationHandler.get("save")}
                    onclick={() => saveEvent(
                        eventId,
                        {
                            repetitionType: repetitionType,
                            repetitionData: nullIfEmpty(repetitionData),
                            repeatForDays: repeatForDays,
                            startDate: new Optional(startDate).map(d => d.toString()).orElse(null),
                            endDate: new Optional(nullIfEmpty(endDate)).map(d => d.toString()).orElse(null),
                            time: new Optional(time).map(d => d.formatWithoutSeconds()).orElse(null),
                            title: title,
                            content: content,
                            remindMeBeforeDays: remindMeBeforeDays
                        },
                        existingLabels,
                        setDisplaySpinner,
                        newLabels,
                        setConfirmationDialogData
                    )}
                />,
                <Button
                    key="cancel"
                    id="calendar-edit-event-confirm-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }
}

export default CalendarEditEventPage;