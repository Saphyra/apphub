import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./expired_events_page_localization.json";
import "./expired_events.css";
import { useEffect, useState } from "react";
import sessionChecker from "common/js/SessionChecker";
import NotificationService from "common/js/notification/NotificationService";
import useRefresh from "common/hook/Refresh";
import { cacheAndUpdate, cachedOrDefault, hasValue } from "common/js/Utils";
import useLoader from "common/hook/Loader";
import ExpiredEventList from "./component/ExpiredEventList";
import OpenedEvent from "../common/event/opened/OpenedEvent";
import Button from "common/component/input/Button";
import TestableDateInput from "../common/input/TestableDateInput";
import OpenedOccurrence from "../common/occurrence/OpenedOccurrence";
import Footer from "common/component/Footer";
import ConfirmationDialog from "common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "common/component/Spinner";
import { ToastContainer } from "react-toastify";
import LocalDate from "common/js/date/LocalDate";
import ConfirmationDialogData from "common/component/confirmation_dialog/ConfirmationDialogData";
import { CALENDAR_EXPIRED_EVENTS_PAGE, CALENDAR_EXTEND_EXPIRED_EVENT, CALENDAR_GET_EXPIRED_EVENTS, CALENDAR_HIDE_EXPIRED_EVENT, CALENDAR_PAGE } from "../CalendarEndpoints";

const CACHE_KEY_SELECTED_EVENT = "calendar.expiredEvent.selected";
const CACHE_KEY_SELECTED_OCCURRENCE = "calendar.expiredEvent.selectedOccurrence";

const ExpiredEventsPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    document.title = localizationHandler.get("title");
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(0);
    const [refreshCounter, refresh] = useRefresh();

    const [expiredEvents, setExpiredEvents] = useState([]);
    const [selectedEvent, setSelectedEvent] = useState(cachedOrDefault(CACHE_KEY_SELECTED_EVENT, null));
    const [selectedOccurrence, setSelectedOccurrence] = useState(cachedOrDefault(CACHE_KEY_SELECTED_OCCURRENCE, null));
    const [extendedEndDate, setExtendedEndDate] = useState(null);

    const updateDisplaySpinner = (display) => {
        setDisplaySpinner(prev => prev + (display ? 1 : -1));
    }

    useLoader({
        request: CALENDAR_GET_EXPIRED_EVENTS.createRequest(),
        mapper: setExpiredEvents,
        setDisplaySpinner: updateDisplaySpinner,
        listener: [refreshCounter]
    });

    const changeSelectedEvent = (eventId) => {
        cacheAndUpdate(CACHE_KEY_SELECTED_EVENT, eventId, setSelectedEvent);
        setSelectedOccurrence(null);
        setExtendedEndDate(null);
    }

    const changeSelectedOccurrence = (occurrenceId) => {
        cacheAndUpdate(CACHE_KEY_SELECTED_OCCURRENCE, occurrenceId, setSelectedOccurrence)
    }

    return (
        <div id="calendar-expired-events" className="main-page">
            <main className="headless">
                <ExpiredEventList
                    events={expiredEvents}
                    localizationHandler={localizationHandler}
                    setDisplaySpinner={updateDisplaySpinner}
                    selectedEvent={selectedEvent}
                    setSelectedEvent={changeSelectedEvent}
                />

                {hasValue(selectedEvent) &&
                    <OpenedEvent
                        backUrl={CALENDAR_EXPIRED_EVENTS_PAGE}
                        eventId={selectedEvent}
                        setSelectedEvent={changeSelectedEvent}
                        setDisplaySpinner={updateDisplaySpinner}
                        setConfirmationDialogData={setConfirmationDialogData}
                        refresh={refresh}
                        refreshCounter={refreshCounter}
                        selectedOccurrence={selectedOccurrence}
                        setSelectedOccurrence={changeSelectedOccurrence}
                        renderAdditionalOperations={(event) => [
                            <span className="nowrap" key="extend-until">
                                <Button
                                    id="calendar-expired-event-extend-end-date-button"
                                    label={localizationHandler.get("extend-until")}
                                    onclick={() => extendEndDate(event.eventId)}
                                />

                                <TestableDateInput
                                    id="calendar-expired-event-extend-end-date-input"
                                    value={extendedEndDate}
                                    setDate={setExtendedEndDate}
                                />
                            </span>,
                            <Button
                                id="calendar-expired-event-hide"
                                key="hide"
                                label={localizationHandler.get("hide")}
                                onclick={() => confirmHide(event)}
                            />
                        ]}
                    />
                }

                {hasValue(selectedOccurrence) &&
                    <OpenedOccurrence
                        occurrenceId={selectedOccurrence}
                        setConfirmationDialogData={setConfirmationDialogData}
                        setDisplaySpinner={updateDisplaySpinner}
                        setSelectedOccurrence={changeSelectedOccurrence}
                        refreshCounter={refreshCounter}
                        refresh={refresh}
                        backUrl={CALENDAR_EXPIRED_EVENTS_PAGE}
                    />
                }
            </main>

            <Footer
                rightButtons={[
                    <Button
                        id="back-button"
                        key="back"
                        onclick={() => window.location.href = CALENDAR_PAGE}
                        label={localizationHandler.get("back")}
                    />
                ]} />

            <ToastContainer />

            {confirmationDialogData &&
                <ConfirmationDialog
                    id={confirmationDialogData.id}
                    title={confirmationDialogData.title}
                    content={confirmationDialogData.content}
                    choices={confirmationDialogData.choices}
                />
            }

            {displaySpinner > 0 && <Spinner />}
        </div>
    );

    async function extendEndDate(eventId) {
        if (!hasValue(extendedEndDate)) {
            NotificationService.showError(localizationHandler.get("empty-extended-end-date"));
            return;
        }
        if (extendedEndDate.isBefore(LocalDate.now())) {
            NotificationService.showError(localizationHandler.get("past-extended-end-date"));
            return;
        }

        await CALENDAR_EXTEND_EXPIRED_EVENT.createRequest({ value: extendedEndDate.toString() }, { eventId: eventId })
            .send(updateDisplaySpinner);

        refresh();
        setConfirmationDialogData(null);
        changeSelectedEvent(null);
        setExtendedEndDate(null);
    }

    function confirmHide(event) {
        setConfirmationDialogData(new ConfirmationDialogData(
            "calendar-expired-event-hide-confirmation",
            localizationHandler.get("hide-event-confirmation-title"),
            localizationHandler.get("hide-event-confirmation-content", { title: event.title }),
            [
                <Button
                    key="hide"
                    id="calendar-expired-event-hide-button"
                    label={localizationHandler.get("hide")}
                    onclick={() => hideEvent(event.eventId)}
                />,
                <Button
                    key="cancel"
                    id="calendar-expired-event-hide-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    async function hideEvent(eventId) {
        await CALENDAR_HIDE_EXPIRED_EVENT.createRequest(null, { eventId: eventId })
            .send(updateDisplaySpinner);

        refresh();
        setConfirmationDialogData(null);
        changeSelectedEvent(null);
    }
}

export default ExpiredEventsPage;