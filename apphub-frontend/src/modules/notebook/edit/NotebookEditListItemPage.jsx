import React, { useEffect, useState } from "react";
import localizationData from "./notebook_edit_page_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import { useParams } from "react-router";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import { ToastContainer } from "react-toastify";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import Constants from "../../../common/js/Constants";
import ParentSelector from "../common/parent_selector/ParentSelector";
import ListItemTitle from "../common/list_item_title/ListItemTitle";
import InputField from "../../../common/component/input/InputField";
import "./notebook_edit_page.css";
import save from "./service/NotebookEditListItemSaverService";
import loadItemData from "./service/NotebookEditListItemDataLoader";
import OpenedPageType from "../common/OpenedPageType";
import Spinner from "../../../common/component/Spinner";

const NotebookEditListItemPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { listItemId } = useParams();

    const [parentId, setParentId] = useState(null);
    const [listItemTitle, setListItemTitle] = useState("");
    const [value, setValue] = useState(null);
    const [listItemType, setListItemType] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(false);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => loadItemData(listItemId, setDataFromResponse), []);

    const setDataFromResponse = (response) => {
        setParentId(response.parentId);
        setListItemTitle(response.title);
        setValue(response.value);
        setListItemType(response.type);
    }

    return (
        <div>
            <div id="notebook-edit" className="main-page">
                <Header label={localizationHandler.get("page-title")} />

                <main>
                    <ListItemTitle
                        inputId="notebook-edit-list-item-title"
                        placeholder={localizationHandler.get("edit-list-item-title")}
                        setListItemTitle={setListItemTitle}
                        value={listItemTitle}
                    />

                    <ParentSelector
                        parentId={parentId}
                        setParentId={setParentId}
                        listItemId={listItemId}
                        setDisplaySpinner={setDisplaySpinner}
                    />

                    {listItemType !== null && listItemType === OpenedPageType.LINK &&
                        <InputField
                            id="notebook-edit-content"
                            type="text"
                            placeholder={localizationHandler.get("url")}
                            value={value}
                            onchangeCallback={setValue}
                        />
                    }
                </main>

                <Footer
                    rightButtons={
                        <Button
                            id="notebook-edit-back-button"
                            onclick={() => window.location.href = Constants.NOTEBOOK_PAGE}
                            label={localizationHandler.get("back")}
                        />
                    }
                    centerButtons={
                        <Button
                            id="notebook-edit-save-button"
                            onclick={() => save(listItemTitle, listItemType, value, parentId, listItemId, setDisplaySpinner)}
                            label={localizationHandler.get("save")}
                        />
                    }
                />
            </div>

            <ToastContainer />

            {displaySpinner && <Spinner />}
        </div>
    );
}

export default NotebookEditListItemPage;