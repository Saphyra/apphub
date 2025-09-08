import { useEffect, useState } from "react";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import sessionChecker from "../../../common/js/SessionChecker";
import localizationData from "./localization/calendar_labels_page_localization.json";
import NotificationService from "../../../common/js/notification/NotificationService";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import { ToastContainer } from "react-toastify";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "../../../common/component/Spinner";
import { CALENDAR_PAGE } from "../../../common/js/dao/endpoints/CalendarEndpoints";
import "./calendar_labels.css";
import LabelList from "./component/LabelList";
import Events from "./component/Events";
import { cacheAndUpdate, cachedOrDefault, hasValue } from "../../../common/js/Utils";
import OpenedEvent from "./component/OpenedEvent";
import OpenedOccurrence from "./component/OpenedOccurrence";
import useRefresh from "../../../common/hook/Refresh";

const CACHE_KEY_SELECTED_LABEL = "calendar.labels.selectedLabel";
const CACHE_KEY_SELECTED_EVENT = "calendar.labels.selectedEvent";
const CACHE_KEY_SELECTED_OCCURRENCE = "calendar.labels.selectedOccurrence";

const CalendarLabelsPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    document.title = localizationHandler.get("title");
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(false);
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

    return (
        <div id="calendar-labels" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <LabelList
                    localizationHandler={localizationHandler}
                    setDisplaySpinner={setDisplaySpinner}
                    selectedLabel={selectedLabel}
                    setSelectedLabel={changeSelectedLabel}
                    setConfirmationDialogData={setConfirmationDialogData}
                />

                {hasValue(selectedLabel) &&
                    <Events
                        localizationHandler={localizationHandler}
                        selectedLabel={selectedLabel}
                        setDisplaySpinner={setDisplaySpinner}
                        selectedEvent={selectedEvent}
                        setSelectedEvent={changeSelectedEvent}
                        refreshCounter={refreshCount}
                    />
                }

                {hasValue(selectedEvent) &&
                    <OpenedEvent
                        localizationHandler={localizationHandler}
                        setDisplaySpinner={setDisplaySpinner}
                        eventId={selectedEvent}
                        selectedOccurrence={selectedOccurrence}
                        setSelectedOccurrence={changeSelectedOccurrence}
                    />
                }

                {hasValue(selectedOccurrence) &&
                    <OpenedOccurrence
                        occurrenceId={selectedOccurrence}
                        localizationHandler={localizationHandler}
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

            {displaySpinner && <Spinner />}
        </div>
    );
}

export default CalendarLabelsPage;