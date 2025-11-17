import { useEffect, useState } from "react";
import "./notebook.css";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import localizationData from "./notebook_page_localization.json";
import Footer from "../../../common/component/Footer";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import { ToastContainer } from "react-toastify";
import CategoryTree from "./notebook_modules/CategoryTree";
import Button from "../../../common/component/input/Button";
import Constants from "../../../common/js/Constants";
import OpenedListItem from "./notebook_modules/OpenedListItem";
import OpenedPageType from "../common/OpenedPageType";
import UserSettings, { USER_SETTING_CATEGORY_NOTEBOOK } from "../common/UserSettings";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "../../../common/component/Spinner";
import PinnedItems from "./notebook_modules/pin/PinnedItems";
import { hasValue, throwException } from "../../../common/js/Utils";
import { GET_USER_SETTINGS, SET_USER_SETTINGS } from "../../../common/js/dao/endpoints/UserEndpoints";
import Optional from "../../../common/js/collection/Optional";
import { NOTEBOOK_GET_LIST_ITEM } from "../../../common/js/dao/endpoints/NotebookEndpoints";

const NotebookPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [openedListItem, setOpenedListItemD] = useState(sessionStorage.openedListItem ? JSON.parse(sessionStorage.openedListItem) : { id: null, type: OpenedPageType.CATEGORY });
    const [lastEvent, setLastEvent] = useState(null);
    const [userSettings, setUserSettings] = useState({});
    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(0);

    const setOpenedListItem = (newListItem) => {
        if (!newListItem) {
            throwException("IllegalArgument", "newListItem is null");
        }

        if (!newListItem.type) {
            throwException("IllegalArgument", "newListItem.type is null");
        }

        sessionStorage.openedListItem = JSON.stringify(newListItem);
        setOpenedListItemD(newListItem);
    }

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => loadUserSettings(), []);

    const updateDisplaySpinner = (display) => {
        setDisplaySpinner(prev => prev + (display ? 1 : -1));
    }

    const loadUserSettings = () => {
        const fetch = async () => {
            const response = await GET_USER_SETTINGS.createRequest(null, { category: USER_SETTING_CATEGORY_NOTEBOOK })
                .send(updateDisplaySpinner);

            const parsed = setSettingsFromResponse(response);

            const defaultListItemId = parsed[UserSettings.DEFAULT_LIST_ITEM_ID];
            if (!sessionStorage.openedListItem && hasValue(defaultListItemId)) {
                const listItem = await NOTEBOOK_GET_LIST_ITEM.createRequest(null, { listItemId: defaultListItemId })
                    .send(updateDisplaySpinner);

                setOpenedListItemD({ id: listItem.id, type: listItem.type });
            }
        }
        fetch();
    }

    const setSettingsFromResponse = (response) => {
        const settings = {
        }

        settings[UserSettings.SHOW_ARCHIVED] = response[UserSettings.SHOW_ARCHIVED] === "true";
        settings[UserSettings.DEFAULT_LIST_ITEM_ID] = new Optional(response[UserSettings.DEFAULT_LIST_ITEM_ID])
            .filter(v => v !== "")
            .orElse(null);

        setUserSettings(settings);

        return settings;
    }

    const changeUserSettings = async (key, value) => {
        const payload = {
            category: USER_SETTING_CATEGORY_NOTEBOOK,
            key: key,
            value: value
        };
        const response = await SET_USER_SETTINGS.createRequest(payload)
            .send(updateDisplaySpinner);

        setSettingsFromResponse(response);
    }

    return (
        <div id="notebook" className="main-page">
            <main id="notebook-main" className="headless">
                <div id="notebook-nav-bar">
                    <CategoryTree
                        localizationHandler={localizationHandler}
                        setOpenedListItem={setOpenedListItem}
                        lastEvent={lastEvent}
                        setLastEvent={setLastEvent}
                        userSettings={userSettings}
                        setDisplaySpinner={setDisplaySpinner}
                    />

                    <PinnedItems
                        localizationHandler={localizationHandler}
                        openedListItem={openedListItem}
                        setOpenedListItem={setOpenedListItem}
                        lastEvent={lastEvent}
                        setLastEvent={setLastEvent}
                        userSettings={userSettings}
                        setDisplaySpinner={setDisplaySpinner}
                    />
                </div>

                <OpenedListItem
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    lastEvent={lastEvent}
                    userSettings={userSettings}
                    changeUserSettings={changeUserSettings}
                    setConfirmationDialogData={setConfirmationDialogData}
                    setDisplaySpinner={updateDisplaySpinner}
                />
            </main>

            <Footer
                rightButtons={
                    <Button
                        id="notebook-home-button"
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                }
                centerButtons={
                    <Button
                        id="notebook-new-button"
                        onclick={() => window.location.href = Constants.NOTEBOOK_NEW_PAGE + "/" + (openedListItem.type === OpenedPageType.SEARCH ? null : openedListItem.id)}
                        label={localizationHandler.get("new")}
                    />
                }
            />

            {confirmationDialogData &&
                <ConfirmationDialog
                    id={confirmationDialogData.id}
                    title={confirmationDialogData.title}
                    content={confirmationDialogData.content}
                    choices={confirmationDialogData.choices}
                />
            }

            {displaySpinner > 0 && <Spinner />}

            <ToastContainer />
        </div>
    );
}

export default NotebookPage;