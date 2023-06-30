import React, { useEffect, useState } from "react";
import "./notebook.css";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import localizationData from "./notebook_page_localization.json";
import Footer from "../../../common/component/Footer";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import { ToastContainer } from "react-toastify";
import CategoryTree from "./notebook_modules/CategoryTree";
import PinnedItems from "./notebook_modules/PinnedItems";
import Button from "../../../common/component/input/Button";
import Constants from "../../../common/js/Constants";
import OpenedListItem from "./notebook_modules/OpenedListItem";
import ListItemType from "../common/ListItemType";
import Utils from "../../../common/js/Utils";

const NotebookPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [openedListItem, setOpenedListItemD] = useState(sessionStorage.openedListItem ? JSON.parse(sessionStorage.openedListItem) : { id: null, type: ListItemType.CATEGORY });
    const [lastEvent, setLastEvent] = useState(null);

    const setOpenedListItem = (newListItem) => {
        if (!newListItem) {
            Utils.throwException("IllegalArgument", "newListItem is null");
        }

        if (!newListItem.type) {
            Utils.throwException("IllegalArgument", "newListItem.type is null");
        }

        sessionStorage.openedListItem = JSON.stringify(newListItem);
        setOpenedListItemD(newListItem);
    }

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    return (
        <div id="notebook" className="main-page">
            <main id="notebook-main" className="headless">
                <div id="notebook-nav-bar">
                    <CategoryTree
                        localizationHandler={localizationHandler}
                        setOpenedListItem={setOpenedListItem}
                        lastEvent={lastEvent}
                        setLastEvent={setLastEvent}
                    />

                    <PinnedItems
                        localizationHandler={localizationHandler}
                        setOpenedListItem={setOpenedListItem}
                        lastEvent={lastEvent}
                        setLastEvent={setLastEvent}
                    />
                </div>

                <OpenedListItem
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    lastEvent={lastEvent}
                />
            </main>

            <Footer
                leftButtons={
                    <Button
                        id="notebook-home-button"
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                }
                centerButtons={
                    openedListItem.type === ListItemType.CATEGORY ?
                        <Button
                            id="notebook-new-button"
                            onclick={() => window.location.href = Constants.NOTEBOOK_NEW_PAGE + "/" + openedListItem.id}
                            label={localizationHandler.get("new")}
                        />
                        :
                        []
                }
            />

            <ToastContainer />
        </div>
    );
}

export default NotebookPage;