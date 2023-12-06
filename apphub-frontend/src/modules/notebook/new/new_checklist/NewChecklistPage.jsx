import React, { useEffect, useState } from "react";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import localizationData from "./new_checklist_localization.json";
import { useParams } from "react-router";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Header from "../../../../common/component/Header";
import ListItemTitle from "../../common/list_item_title/ListItemTitle";
import ParentSelector from "../../common/parent_selector/ParentSelector";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import { ToastContainer } from "react-toastify";
import "./new_checklist.css";
import ChecklistItemData from "../../common/checklist_item/ChecklistItemData";
import { addItem } from "./service/NewChecklistItemOperations";
import create from "./service/NewChecklistSaver";
import getItems from "./service/NewChecklistItemAssembler";

const NewChecklistPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);
    const [listItemTitle, setListItemTitle] = useState("");
    const [items, setItems] = useState([new ChecklistItemData(0)]);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    return (
        <div id="notebook-new-checklist" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main id="notebook-new-checklist-main">
                <ListItemTitle
                    inputId="notebook-new-checklist-title"
                    placeholder={localizationHandler.get("checklist-title")}
                    setListItemTitle={setListItemTitle}
                    value={listItemTitle}
                />

                <ParentSelector
                    parentId={parentId}
                    setParentId={setParentId}
                />

                <div id="notebook-new-checklist-content-wrapper">
                    {getItems(items, localizationHandler, setItems)}
                </div>
            </main>

            <Footer
                leftButtons={
                    <Button
                        id="notebook-new-checklist-new-item-button"
                        label={localizationHandler.get("new-item")}
                        onclick={() => addItem(items, setItems)}
                    />
                }

                centerButtons={
                    <Button
                        id="notebook-new-checklist-create-button"
                        label={localizationHandler.get("create")}
                        onclick={() => create(listItemTitle, parentId, items)}
                    />
                }

                rightButtons={[
                    <Button
                        key="back-button"
                        id="notebook-new-checklist-back-button"
                        label={localizationHandler.get("back")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_NEW_PAGE + "/" + parent}
                    />,
                    <Button
                        key="home-button"
                        id="notebook-new-checklist-home-button"
                        label={localizationHandler.get("home")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_PAGE}
                    />
                ]}
            />

            <ToastContainer />
        </div>
    );
}

export default NewChecklistPage;