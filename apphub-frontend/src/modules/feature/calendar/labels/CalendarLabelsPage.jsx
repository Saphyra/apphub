import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./calendar_labels_page_localization.json";
import "./calendar_labels.css";
import { useEffect, useState } from "react";
import sessionChecker from "common/js/SessionChecker";
import NotificationService from "common/js/notification/NotificationService";
import useRefresh from "common/hook/Refresh";
import { cacheAndUpdate, cachedOrDefault, hasValue } from "common/js/Utils";
import Header from "common/component/Header";
import LabelList from "./component/LabelList";
import Events from "./component/Events";
import OpenedEvent from "../common/event/opened/OpenedEvent";
import OpenedOccurrence from "../common/occurrence/OpenedOccurrence";
import Footer from "common/component/Footer";
import Button from "common/component/input/Button";
import { ToastContainer } from "react-toastify";
import ConfirmationDialog from "common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "common/component/Spinner";
import { CALENDAR_LABELS_PAGE, CALENDAR_PAGE } from "../CalendarEndpoints";

const CACHE_KEY_SELECTED_LABEL = "calendar.labels.selectedLabel";
const CACHE_KEY_SELECTED_EVENT = "calendar.labels.selectedEvent";
const CACHE_KEY_SELECTED_OCCURRENCE = "calendar.labels.selectedOccurrence";

const CalendarLabelsPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    document.title = localizationHandler.get("title");
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(0);
    const [refreshCount, refresh] = useRefresh();

    const [selectedLabel, setSelectedLabel] = useState(cachedOrDefault(CACHE_KEY_SELECTED_LABEL, null));
    const [selectedEvent, setSelectedEvent] = useState(cachedOrDefault(CACHE_KEY_SELECTED_EVENT, null));
    const [selectedOccurrence, setSelectedOccurrence] = useState(cachedOrDefault(CACHE_KEY_SELECTED_OCCURRENCE, null));

    const changeSelectedLabel = (labelId) => {
        cacheAndUpdate(CACHE_KEY_SELECTED_LABEL, labelId, setSelectedLabel);
        changeSelectedEvent(null);
    }

    const changeSelectedEvent = (eventId) => {
        cacheAndUpdate(CACHE_KEY_SELECTED_EVENT, eventId, setSelectedEvent);
        setSelectedOccurrence(null);
    }

    const changeSelectedOccurrence = (occurrenceId) => {
        cacheAndUpdate(CACHE_KEY_SELECTED_OCCURRENCE, occurrenceId, setSelectedOccurrence)
    }

    const updateDisplaySpinner = (display) => {
        setDisplaySpinner(prev => prev + (display ? 1 : -1));
    }

    return (
        <div id="calendar-labels" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <LabelList
                    localizationHandler={localizationHandler}
                    setDisplaySpinner={updateDisplaySpinner}
                    selectedLabel={selectedLabel}
                    setSelectedLabel={changeSelectedLabel}
                    setConfirmationDialogData={setConfirmationDialogData}
                />

                <Events
                    localizationHandler={localizationHandler}
                    selectedLabel={selectedLabel}
                    setDisplaySpinner={updateDisplaySpinner}
                    selectedEvent={selectedEvent}
                    setSelectedEvent={changeSelectedEvent}
                    refreshCounter={refreshCount}
                />

                {hasValue(selectedEvent) &&
                    <OpenedEvent
                        backUrl={CALENDAR_LABELS_PAGE}
                        setDisplaySpinner={updateDisplaySpinner}
                        eventId={selectedEvent}
                        selectedOccurrence={selectedOccurrence}
                        setSelectedOccurrence={changeSelectedOccurrence}
                        refreshCounter={refreshCount}
                        setConfirmationDialogData={setConfirmationDialogData}
                        setSelectedEvent={changeSelectedEvent}
                        refresh={refresh}
                    />
                }

                {hasValue(selectedOccurrence) &&
                    <OpenedOccurrence
                        occurrenceId={selectedOccurrence}
                        localizationHandler={localizationHandler}
                        setConfirmationDialogData={setConfirmationDialogData}
                        setDisplaySpinner={updateDisplaySpinner}
                        setSelectedOccurrence={changeSelectedOccurrence}
                        refreshCounter={refreshCount}
                        refresh={refresh}
                        backUrl={CALENDAR_LABELS_PAGE}
                    />
                }
            </main>

            <Footer rightButtons={[
                <Button
                    id="calendar-labels-back-button"
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

export default CalendarLabelsPage;