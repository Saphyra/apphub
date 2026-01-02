import { useEffect, useRef, useState } from "react";
import Footer from "../../../common/component/Footer";
import { ToastContainer } from "react-toastify";
import localizationData from "./localization/calendar_page_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import Button from "../../../common/component/input/Button";
import Constants from "../../../common/js/Constants";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "../../../common/component/Spinner";
import { MONTH, View } from "./common/View";
import ViewSelector from "./component/navigation/ViewSelector";
import LocalDate from "../../../common/js/date/LocalDate";
import ReferenceDateSelector from "./component/navigation/ReferenceDateSelector";
import "./calendar.css";
import CalendarContent from "./component/content/CalendarContent";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import { cacheAndUpdate, cachedOrDefault } from "../../../common/js/Utils";
import useRefresh from "../../../common/hook/Refresh";
import useHasFocus from "../../../common/hook/UseHasFocus";
import { useUpdateEffect } from "react-use";
import RightPanel from "./component/right_panel/RightPanel";
import Labels from "./component/navigation/Labels";

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
    const [displaySpinner, setDisplaySpinner] = useState(0);
    const [viewName, setViewName] = useState(cachedOrDefault(CACHE_KEY_VIEW, MONTH));
    const [referenceDate, setReferenceDate] = useState(cachedOrDefault(CACHE_KEY_REFERENCE_DATE, LocalDate.now(), v => LocalDate.parse(v)));
    const [activeLabel, setActiveLabel] = useState(cachedOrDefault(CACHE_KEY_ACTIVE_LABEL, null));
    const [selectedDate, setSelectedDate] = useState(cachedOrDefault(CACHE_KEY_SELECTED_DATE, LocalDate.now(), v => LocalDate.parse(v)));
    const [selectedOccurrence, setSelectedOccurrence] = useState(cachedOrDefault(CACHE_KEY_SELECTED_OCCURRENCE, null));

    const [currentDate, setCurrentDate] = useState(LocalDate.now());
    const [refreshCounter, refresh] = useRefresh();
    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus) {
            refresh();

            const now = LocalDate.now();
            if (!currentDate.equals(now)) {
                setCurrentDate(now);
                setReferenceDate(now);
                setSelectedDate(now);
            }
        }
    }, [isInFocus]);

    const updateDisplaySpinner = (display) => {
        setDisplaySpinner(prev => prev + (display ? 1 : -1));
    }

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

                        <div id="calendar-navigation-selected-date" className="nowrap">
                            {View[viewName].format(referenceDate)}
                        </div>
                    </div>

                    <Labels
                        activeLabel={activeLabel}
                        setActiveLabel={v => cacheAndUpdate(CACHE_KEY_ACTIVE_LABEL, v, setActiveLabel)}
                    />

                    <CalendarContent
                        view={View[viewName]}
                        activeLabel={activeLabel}
                        setDisplaySpinner={updateDisplaySpinner}
                        referenceDate={referenceDate}
                        selectedDate={selectedDate}
                        setSelectedDate={v => cacheAndUpdate(CACHE_KEY_SELECTED_DATE, v, setSelectedDate, v => LocalDate.parse(v))}
                        setSelectedOccurrence={v => cacheAndUpdate(CACHE_KEY_SELECTED_OCCURRENCE, v, setSelectedOccurrence)}
                        refreshCounter={refreshCounter}
                    />
                </div>

                <RightPanel
                    selectedDate={selectedDate}
                    activeLabel={activeLabel}
                    setDisplaySpinner={updateDisplaySpinner}
                    selectedOccurrence={selectedOccurrence}
                    setSelectedOccurrence={v => cacheAndUpdate(CACHE_KEY_SELECTED_OCCURRENCE, v, setSelectedOccurrence)}
                    refreshCounter={refreshCounter}
                    refresh={refresh}
                    setConfirmationDialogData={setConfirmationDialogData}
                    localizationHandler={localizationHandler}
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

export default CalendarPage;