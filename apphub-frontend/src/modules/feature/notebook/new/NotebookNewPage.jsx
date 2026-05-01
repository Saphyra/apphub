import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./notebook_new_page_localization.json";
import "./notebook_new.css";
import { useParams } from "react-router";
import { useEffect } from "react";
import NotificationService from "common/js/notification/NotificationService";
import MapStream from "common/js/collection/MapStream";
import Button from "common/component/input/Button";
import Header from "common/component/Header";
import Footer from "common/component/Footer";
import { ToastContainer } from "react-toastify";
import listItemTypes from "./list_item_types.json";
import { NOTEBOOK_PAGE } from "../NotebookEndpoints";

const NotebookNewPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    console.log(parent);

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
                rightButtons={
                    <Button
                        id="notebook-new-back-button"
                        onclick={() => window.location.href = NOTEBOOK_PAGE}
                        label={localizationHandler.get("back")}
                    />
                }
            />

            <ToastContainer />
        </div>
    );
}

export default NotebookNewPage;