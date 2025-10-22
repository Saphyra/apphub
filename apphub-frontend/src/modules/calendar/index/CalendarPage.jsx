import { useEffect, useState } from "react";
import Footer from "../../../common/component/Footer";
import { ToastContainer } from "react-toastify";
import localizationData from "./localization/calendar_page_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import Button from "../../../common/component/input/Button";
import Constants from "../../../common/js/Constants";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "../../../common/component/Spinner";
import { MONTH, View } from "./common/View";
import ViewSelector from "./component/ViewSelector";
import LocalDate from "../../../common/js/date/LocalDate";
import ReferenceDateSelector from "./component/ReferenceDateSelector";
import SelectedDateDispalyer from "./component/SelectedDateDisplayer";
import "./calendar.css";
import Labels from "./component/Labels";
import CalendarContent from "./component/content/CalendarContent";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import { cacheAndUpdate, cachedOrDefault, hasValue } from "../../../common/js/Utils";
import SelectedOccurrence from "./component/SelectedOccurrence";
import useRefresh from "../../../common/hook/Refresh";
import SelectedDate from "./component/SelectedDate";
import useHasFocus from "../../../common/hook/UseHasFocus";
import { useUpdateEffect } from "react-use";

const CACHE_KEY_VIEW = "calendar.view";
const CACHE_KEY_REFERENCE_DATE = "calendar.referenceDate";
const CACHE_KEY_ACTIVE_LABEL = "calendar.activeLabel";
const CACHE_KEY_SELECTED_DATE = "calendar.selectedDate";
const CACHE_KEY_SELECTED_OCCURRENCE = "calendar.selectedOccurrence";

const CalendarPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    document.title = localizationHandler.get("title");
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [viewName, setViewName] = useState(cachedOrDefault(CACHE_KEY_VIEW, MONTH));
    const [referenceDate, setReferenceDate] = useState(cachedOrDefault(CACHE_KEY_REFERENCE_DATE, LocalDate.now(), v => LocalDate.parse(v)));
    const [activeLabel, setActiveLabel] = useState(cachedOrDefault(CACHE_KEY_ACTIVE_LABEL, null));
    const [selectedDate, setSelectedDate] = useState(cachedOrDefault(CACHE_KEY_SELECTED_DATE, LocalDate.now(), v => LocalDate.parse(v)));
    const [selectedOccurrence, setSelectedOccurrence] = useState(cachedOrDefault(CACHE_KEY_SELECTED_OCCURRENCE, null));
    const [refreshCounter, refresh] = useRefresh();
    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus) {
            refresh();
        }
    }, [isInFocus]);

    return (
        <div id="calendar" className="main-page">
            <main className="headless">
                <div id="calendar-content-wrapper">
                    <div id="calendar-navigation">
                        <ViewSelector
                            view={viewName}
                            setView={v => cacheAndUpdate(CACHE_KEY_VIEW, v, setViewName)}
                        />

                        <ReferenceDateSelector
                            referenceDate={referenceDate}
                            setReferenceDate={v => cacheAndUpdate(CACHE_KEY_REFERENCE_DATE, v, setReferenceDate, v => LocalDate.parse(v))}
                            view={View[viewName]}
                        />

                        <SelectedDateDispalyer
                            referenceDate={referenceDate}
                            view={View[viewName]}
                        />
                    </div>

                    <Labels
                        activeLabel={activeLabel}
                        setActiveLabel={v => cacheAndUpdate(CACHE_KEY_ACTIVE_LABEL, v, setActiveLabel)}
                    />

                    <CalendarContent
                        view={View[viewName]}
                        activeLabel={activeLabel}
                        setDisplaySpinner={setDisplaySpinner}
                        referenceDate={referenceDate}
                        selectedDate={selectedDate}
                        setSelectedDate={v => cacheAndUpdate(CACHE_KEY_SELECTED_DATE, v, setSelectedDate, v => LocalDate.parse(v))}
                        setSelectedOccurrence={v => cacheAndUpdate(CACHE_KEY_SELECTED_OCCURRENCE, v, setSelectedOccurrence)}
                        refreshCounter={refreshCounter}
                    />
                </div>

                <SelectedDate
                    selectedDate={selectedDate}
                    activeLabel={activeLabel}
                    setDisplaySpinner={setDisplaySpinner}
                    setSelectedOccurrence={v => cacheAndUpdate(CACHE_KEY_SELECTED_OCCURRENCE, v, setSelectedOccurrence)}
                    refreshCounter={refreshCounter}
                />
            </main>

            <Footer rightButtons={[
                <Button
                    id="calendar-home-button"
                    key="home"
                    onclick={() => window.location.href = Constants.MODULES_PAGE}
                    label={localizationHandler.get("home")}
                />
            ]} />

            <ToastContainer />

            {selectedOccurrence &&
                <SelectedOccurrence
                    occurrenceId={selectedOccurrence}
                    setDisplaySpinner={setDisplaySpinner}
                    localizationHandler={localizationHandler}
                    setSelectedOccurrence={v => cacheAndUpdate(CACHE_KEY_SELECTED_OCCURRENCE, v, setSelectedOccurrence)}
                    refresh={refresh}
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            }

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

export default CalendarPage;