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
import Stream from "../../../../common/js/collection/Stream";
import Utils from "../../../../common/js/Utils";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";
import Endpoints from "../../../../common/js/dao/dao";
import ChecklistItem from "../../common/checklist_item/ChecklistItem";
import ChecklistItemData from "../../common/checklist_item/ChecklistItemData";
import MoveDirection from "../../common/MoveDirection";

const NewChecklistPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);
    const [listItemTitle, setListItemTitle] = useState("");
    const [items, setItems] = useState([new ChecklistItemData(0)]);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const addItem = () => {
        const maxOrder = new Stream(items)
            .map(item => item.index)
            .max()
            .orElse(0);

        const newRow = new ChecklistItemData(maxOrder + 1);
        const copy = new Stream(items)
            .add(newRow)
            .toList();

        setItems(copy);
    }

    const updateItem = () => {
        const copy = new Stream(items)
            .toList();
        setItems(copy);
    }

    const removeItem = (item) => {
        const copy = new Stream(items)
            .remove(i => i === item)
            .toList();
        setItems(copy);
    }

    const moveItem = (item, moveDirection) => {
        const orderedItems = new Stream(items)
            .sorted((a, b) => a.index - b.index)
            .toList();

        const itemIndex = orderedItems.indexOf(item);

        let newIndex;
        switch (moveDirection) {
            case MoveDirection.UP:
                newIndex = itemIndex - 1;
                break;
            case MoveDirection.DOWN:
                newIndex = itemIndex + 1;
                break;
            default:
                Utils.throwException("IllegalArgument", "Unhandled MoveDirection: " + moveDirection);
        }

        const otherItem = orderedItems[newIndex];

        if (!otherItem) {
            //Item is at the top/bottom of the list and cannot be moved further.
            return;
        }

        const originalOrder = item.index;
        const newOrder = otherItem.index;

        item.index = newOrder;
        otherItem.index = originalOrder;

        updateItem();
    }

    const create = async () => {
        const result = validateListItemTitle(listItemTitle);
        if (!result.valid) {
            NotificationService.showError(result.message);
            return;
        }

        const payload = {
            parent: parentId,
            title: listItemTitle,
            items: items
        }

        await Endpoints.NOTEBOOK_CREATE_CHECKLIST.createRequest(payload)
            .send();

        window.location.href = Constants.NOTEBOOK_PAGE;
    }

    const getItems = () => {
        if (items.length === 0) {
            return (
                <div id="notebook-new-checklist-no-items">{localizationHandler.get("no-items")}</div>
            );
        }

        return new Stream(items)
            .sorted((a, b) => a.index - b.index)
            .map(item =>
                <ChecklistItem
                    key={item.index}
                    localizationHandler={localizationHandler}
                    item={item}
                    updateItem={updateItem}
                    removeItem={removeItem}
                    moveItem={moveItem}
                />
            )
            .toList();
    }

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
                    {getItems()}
                </div>
            </main>

            <Footer
                leftButtons={
                    <Button
                        id="notebook-new-checklist-new-item-button"
                        label={localizationHandler.get("new-item")}
                        onclick={() => addItem()}
                    />
                }

                centerButtons={
                    <Button
                        id="notebook-new-checklist-create-button"
                        label={localizationHandler.get("create")}
                        onclick={() => create()}
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