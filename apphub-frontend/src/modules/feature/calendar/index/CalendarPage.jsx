import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./localization/calendar_page_localization.json";
import "./calendar.css";
import { useEffect, useState } from "react";
import sessionChecker from "common/js/SessionChecker";
import NotificationService from "common/js/notification/NotificationService";
import { cacheAndUpdate, cachedOrDefault, hasValue, isBlank } from "common/js/Utils";
import LocalDate from "common/js/date/LocalDate";
import useRefresh from "common/hook/Refresh";
import useHasFocus from "common/hook/UseHasFocus";
import { useUpdateEffect } from "react-use";
import ViewSelector from "./component/navigation/ViewSelector";
import ReferenceDateSelector from "./component/navigation/ReferenceDateSelector";
import { MONTH, View } from "./common/View";
import Labels from "./component/navigation/Labels";
import CalendarContent from "./component/content/CalendarContent";
import RightPanel from "./component/right_panel/RightPanel";
import Footer from "common/component/Footer";
import PostLabeledInputField from "common/component/input/PostLabeledInputField";
import InputField from "common/component/input/InputField";
import ExpiredEventNotification from "./component/ExpiredEventNotification";
import Button from "common/component/input/Button";
import ConfirmationDialog from "common/component/confirmation_dialog/ConfirmationDialog";
import { ToastContainer } from "react-toastify";
import Spinner from "common/component/Spinner";
import { USER_SETTING_CATEGORY_CALENDAR, UserSettings } from "../common/UserSettings";
import Optional from "common/js/collection/Optional";
import { CALENDAR_SEARCH_PAGE } from "../CalendarEndpoints";
import { GET_USER_SETTINGS, SET_USER_SETTINGS } from "common/js/GenericEndpoints";
import { MODULES_PAGE } from "modules/etc/modules/ModulesEndpoints";

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
    const [viewName, setViewName] = useState(null);
    const [referenceDate, setReferenceDate] = useState(cachedOrDefault(CACHE_KEY_REFERENCE_DATE, LocalDate.now(), v => LocalDate.parse(v)));
    const [activeLabel, setActiveLabel] = useState(cachedOrDefault(CACHE_KEY_ACTIVE_LABEL, null));
    const [selectedDate, setSelectedDate] = useState(cachedOrDefault(CACHE_KEY_SELECTED_DATE, LocalDate.now(), v => LocalDate.parse(v)));
    const [selectedOccurrence, setSelectedOccurrence] = useState(cachedOrDefault(CACHE_KEY_SELECTED_OCCURRENCE, null));

    const [currentDate, setCurrentDate] = useState(LocalDate.now());
    const [showArchived, setShowArchived] = useState(true);
    const [refreshCounter, refresh] = useRefresh();
    const isInFocus = useHasFocus();
    useUpdateEffect(
        () => {
            if (isInFocus) {
                refresh();

                const now = LocalDate.now();
                if (!currentDate.equals(now)) {
                    setCurrentDate(now);
                    setReferenceDate(now);
                    setSelectedDate(now);
                }
            }
        },
        [isInFocus]
    );
    useEffect(loadUserSettings, []);

    const updateDisplaySpinner = (display) => {
        setDisplaySpinner(prev => prev + (display ? 1 : -1));
    }

    return (
        <div id="calendar" className="main-page">
            <main className="headless">
                {hasValue(viewName) &&
                    <div id="calendar-content-wrapper">

                        <div id="calendar-navigation">
                            <ViewSelector
                                view={viewName}
                                setView={updateView}
                            />

                            <ReferenceDateSelector
                                referenceDate={referenceDate}
                                setReferenceDate={v => cacheAndUpdate(CACHE_KEY_REFERENCE_DATE, v, setReferenceDate, v => LocalDate.parse(v))}
                                view={View[viewName]}
                                localizationHandler={localizationHandler}
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
                            showArchived={showArchived}
                            setDisplaySpinner={updateDisplaySpinner}
                            referenceDate={referenceDate}
                            selectedDate={selectedDate}
                            setSelectedDate={v => cacheAndUpdate(CACHE_KEY_SELECTED_DATE, v, setSelectedDate, v => LocalDate.parse(v))}
                            setSelectedOccurrence={v => cacheAndUpdate(CACHE_KEY_SELECTED_OCCURRENCE, v, setSelectedOccurrence)}
                            refreshCounter={refreshCounter}
                        />
                    </div>
                }

                <RightPanel
                    selectedDate={selectedDate}
                    activeLabel={activeLabel}
                    showArchived={showArchived}
                    setDisplaySpinner={updateDisplaySpinner}
                    selectedOccurrence={selectedOccurrence}
                    setSelectedOccurrence={v => cacheAndUpdate(CACHE_KEY_SELECTED_OCCURRENCE, v, setSelectedOccurrence)}
                    refreshCounter={refreshCounter}
                    refresh={refresh}
                    setConfirmationDialogData={setConfirmationDialogData}
                    localizationHandler={localizationHandler}
                />
            </main>

            <Footer
                leftButtons={[
                    <PostLabeledInputField
                        key="show-archived"
                        id="calendar-show-archived"
                        label={localizationHandler.get("show-archived")}
                        input={<InputField
                            id="calendar-show-archived-checkbox"
                            type="checkbox"
                            checked={showArchived}
                            onchangeCallback={updateShowArchived}
                        />}
                    />,
                    <ExpiredEventNotification
                        key="expired-event-notification"
                        setDisplaySpinner={updateDisplaySpinner}
                        localizationHandler={localizationHandler}
                        refreshCounter={refreshCounter}
                    />
                ]}
                centerButtons={[
                    <Button
                        key="search"
                        id="calendar-search-button"
                        label={localizationHandler.get("search")}
                        onclick={() => window.location.href = CALENDAR_SEARCH_PAGE}
                    />
                ]}
                rightButtons={[
                    <Button
                        id="calendar-home-button"
                        key="home"
                        onclick={() => window.location.href = MODULES_PAGE}
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

    function loadUserSettings() {
        const fetch = async () => {
            const response = await GET_USER_SETTINGS.createRequest(null, { category: USER_SETTING_CATEGORY_CALENDAR })
                .send(updateDisplaySpinner);

            setShowArchived(response[UserSettings.SHOW_ARCHIVED] === "true");

            new Optional(response[UserSettings.INDEX_VIEW_LAYOUT])
                .filter(v => !isBlank(v))
                .or(() => MONTH)
                .ifPresent(setViewName);
        }
        fetch();
    }

    async function updateView(newView) {
        setViewName(newView);

        const payload = {
            category: USER_SETTING_CATEGORY_CALENDAR,
            key: UserSettings.INDEX_VIEW_LAYOUT,
            value: newView
        }

        await SET_USER_SETTINGS.createRequest(payload)
            .send(updateDisplaySpinner);
    }

    async function updateShowArchived(checked) {
        setShowArchived(checked);

        const payload = {
            category: USER_SETTING_CATEGORY_CALENDAR,
            key: UserSettings.SHOW_ARCHIVED,
            value: checked
        }

        await SET_USER_SETTINGS.createRequest(payload)
            .send(updateDisplaySpinner);

        refresh();
    }
}

export default CalendarPage;