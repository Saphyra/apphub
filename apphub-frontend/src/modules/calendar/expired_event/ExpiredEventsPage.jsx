import { useEffect, useState } from "react";
import { CALENDAR_EXPIRED_EVENTS_PAGE, CALENDAR_GET_EXPIRED_EVENTS, CALENDAR_PAGE } from "../../../common/js/dao/endpoints/CalendarEndpoints";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import sessionChecker from "../../../common/js/SessionChecker";
import localizationData from "./expired_events_page_localization.json";
import NotificationService from "../../../common/js/notification/NotificationService";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import { ToastContainer } from "react-toastify";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "../../../common/component/Spinner";
import useLoader from "../../../common/hook/Loader";
import "./expired_events.css";
import ExpiredEventList from "./component/ExpiredEventList";
import { cacheAndUpdate, cachedOrDefault, hasValue } from "../../../common/js/Utils";
import OpenedExpiredEvent from "./component/OpenedExpiredEvent";
import useRefresh from "../../../common/hook/Refresh";
import OpenedOccurrence from "../common/occurrence/OpenedOccurrence";

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
                    setDisplaySpinner={setDisplaySpinner}
                    selectedEvent={selectedEvent}
                    setSelectedEvent={changeSelectedEvent}
                />

                {hasValue(selectedEvent) &&
                    <OpenedExpiredEvent
                        eventId={selectedEvent}
                        setSelectedEvent={changeSelectedEvent}
                        setDisplaySpinner={setDisplaySpinner}
                        localizationHandler={localizationHandler}
                        setConfirmationDialogData={setConfirmationDialogData}
                        refresh={refresh}
                        refreshCounter={refreshCounter}
                        selectedOccurrence={selectedOccurrence}
                        setSelectedOccurrence={changeSelectedOccurrence}
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
}

export default ExpiredEventsPage;