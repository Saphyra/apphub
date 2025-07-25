import React, { useState } from "react";
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
import SelectedDate from "./component/selected_date/SelectedDate";

const KEY_SESSION_STORAGE_VIEW = "calendarView"

const CalendarPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    document.title = localizationHandler.get("title");

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [viewName, setViewName] = useState(sessionStorage[KEY_SESSION_STORAGE_VIEW] || MONTH);
    const [referenceDate, setReferenceDate] = useState(LocalDate.now());
    const [activeLabel, setActiveLabel] = useState(null);
    const [selectedDate, setSelectedDate] = useState(LocalDate.now());

    return (
        <div id="calendar" className="main-page">
            <main className="headless">
                <div id="calendar-content-wrapper">
                    <div id="calendar-navigation">
                        <ViewSelector
                            view={viewName}
                            setView={changeView}
                        />

                        <ReferenceDateSelector
                            referenceDate={referenceDate}
                            setReferenceDate={setReferenceDate}
                            view={View[viewName]}
                        />

                        <SelectedDateDispalyer
                            referenceDate={referenceDate}
                            view={View[viewName]}
                        />
                    </div>

                    <Labels
                        activeLabel={activeLabel}
                        setActiveLabel={setActiveLabel}
                    />

                    <CalendarContent
                        view={View[viewName]}
                        activeLabel={activeLabel}
                        setDisplaySpinner={setDisplaySpinner}
                        referenceDate={referenceDate}
                        selectedDate={selectedDate}
                        setSelectedDate={setSelectedDate}
                    />
                </div>

                <SelectedDate
                    selectedDate={selectedDate}
                    activeLabel={activeLabel}
                    setDisplaySpinner={setDisplaySpinner}
                />
            </main>

            <Footer rightButtons={[
                <Button
                    id="notebook-home-button"
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

            {displaySpinner && <Spinner />}
        </div>
    );

    function changeView(newView) {
        sessionStorage[KEY_SESSION_STORAGE_VIEW] = newView;
        setViewName(newView);
    }
}

export default CalendarPage;