import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./calendar_search_page_localization.json";
import "./calendar_search.css";
import { useEffect, useState } from "react";
import NotificationService from "common/js/notification/NotificationService";
import useRefresh from "common/hook/Refresh";
import { cacheAndUpdate, cachedOrDefault, hasValue } from "common/js/Utils";
import useLoader from "common/hook/Loader";
import Header from "common/component/Header";
import InputField from "common/component/input/InputField";
import OpenedEvent from "../common/event/opened/OpenedEvent";
import OpenedOccurrence from "../common/occurrence/OpenedOccurrence";
import Footer from "common/component/Footer";
import Button from "common/component/input/Button";
import { ToastContainer } from "react-toastify";
import ConfirmationDialog from "common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "common/component/Spinner";
import EventList from "./component/EventList";
import { CALENDAR_PAGE, CALENDAR_SEARCH_EVENTS, CALENDAR_SEARCH_PAGE } from "../CalendarEndpoints";

const CACHE_KEY_QUERY = "calendar.searchEvent.query";
const CACHE_KEY_SELECTED_EVENT = "calendar.searchEvent.selected";
const CACHE_KEY_SELECTED_OCCURRENCE = "calendar.searchEvent.selectedOccurrence";

const CalendarSearchPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(0);
    const [refreshCount, refresh] = useRefresh();

    const [searchText, setSearchText] = useState(cachedOrDefault(CACHE_KEY_QUERY, ""));
    const [events, setEvents] = useState([]);
    const [selectedEvent, setSelectedEvent] = useState(cachedOrDefault(CACHE_KEY_SELECTED_EVENT, null));
    const [selectedOccurrence, setSelectedOccurrence] = useState(cachedOrDefault(CACHE_KEY_SELECTED_OCCURRENCE, null));

    const updateDisplaySpinner = (display) => {
        setDisplaySpinner(prev => prev + (display ? 1 : -1));
    }

    useLoader({
        request: CALENDAR_SEARCH_EVENTS.createRequest({ value: searchText }),
        mapper: setEvents,
        listener: [searchText, refreshCount],
        setDisplaySpinner: updateDisplaySpinner,
        condition: () => searchText.length >= 3
    });

    const changeSelectedEvent = (eventId) => {
        cacheAndUpdate(CACHE_KEY_SELECTED_EVENT, eventId, setSelectedEvent);
        setSelectedOccurrence(null);
    }

    const changeSelectedOccurrence = (occurrenceId) => {
        cacheAndUpdate(CACHE_KEY_SELECTED_OCCURRENCE, occurrenceId, setSelectedOccurrence)
    }

    return (
        <div id="calendar-search" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <div id="calendar-search-content">
                    <InputField
                        id="calendar-search-input"
                        value={searchText}
                        onchangeCallback={(value) => cacheAndUpdate(CACHE_KEY_QUERY, value, setSearchText)}
                        placeholder={localizationHandler.get("search-placeholder")}
                    />

                    {getContent()}
                </div>

                {hasValue(selectedEvent) &&
                    <OpenedEvent
                        eventId={selectedEvent}
                        backUrl={CALENDAR_SEARCH_PAGE}
                        setSelectedEvent={changeSelectedEvent}
                        setDisplaySpinner={updateDisplaySpinner}
                        setConfirmationDialogData={setConfirmationDialogData}
                        refresh={refresh}
                        refreshCounter={refreshCount}
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
                        refreshCounter={refreshCount}
                        refresh={refresh}
                        backUrl={CALENDAR_SEARCH_PAGE}
                    />
                }
            </main>

            <Footer rightButtons={[
                <Button
                    id="calendar-search-back-button"
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

    function getContent() {
        if (searchText.length < 3) {
            return (
                <div className="calendar-search-error">{localizationHandler.get("search-text-too-short")}</div>
            );
        }

        if (events.length === 0) {
            return (
                <div className="calendar-search-error">{localizationHandler.get("no-results")}</div>
            );
        }

        return <EventList
            events={events}
            selectedEvent={selectedEvent}
            setSelectedEvent={changeSelectedEvent}
        />
    }
};

export default CalendarSearchPage;