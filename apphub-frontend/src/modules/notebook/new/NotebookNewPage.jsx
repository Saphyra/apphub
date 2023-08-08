import React, { useEffect } from "react";

import "./notebook_new.css";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import localizationData from "./notebook_new_page_localization.json";
import Footer from "../../../common/component/Footer";
import Header from "../../../common/component/Header";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import { ToastContainer } from "react-toastify";
import Button from "../../../common/component/input/Button";
import Constants from "../../../common/js/Constants";
import listItemTypes from "./list_item_types.json"
import MapStream from "../../../common/js/collection/MapStream";
import { useParams } from "react-router";

const NotebookNewPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const getListItemTypes = () => {
        return new MapStream(listItemTypes)
            .map((listItemType, url) => {
                return {
                    url: url,
                    label: localizationHandler.get(listItemType)
                }
            })
            .sorted((a, b) => a.value.label.localeCompare(b.value.label))
            .map((listItemType, listItem) =>
                <Button
                    key={listItemType}
                    className="notebook-new-list-item"
                    id={"notebook-new-" + listItemType}
                    label={listItem.label}
                    onclick={() => window.location.href = listItem.url + "/" + parent}
                />
            )
            .toList();
    }

    return (
        <div id="notebook-new" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main id="notebook-new-main">
                {getListItemTypes()}
            </main>

            <Footer
                leftButtons={
                    <Button
                        id="notebook-new-back-button"
                        onclick={() => window.location.href = Constants.NOTEBOOK_PAGE}
                        label={localizationHandler.get("back")}
                    />
                }
            />

            <ToastContainer />
        </div>
    );
}

export default NotebookNewPage;