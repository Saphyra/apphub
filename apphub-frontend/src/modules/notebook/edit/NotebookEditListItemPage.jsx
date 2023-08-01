import React, { useEffect, useState } from "react";
import localizationData from "./resources/notebook_edit_page_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import { useParams } from "react-router";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import { ToastContainer } from "react-toastify";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import Constants from "../../../common/js/Constants";
import Endpoints from "../../../common/js/dao/dao";
import ParentSelector from "../common/parent_selector/ParentSelector";
import ListItemTitle from "../common/list_item_title/ListItemTitle";
import validateListItemTitle from "../common/validator/ListItemTitleValidator";
import InputField from "../../../common/component/input/InputField";
import ListItemType from "../common/ListItemType";
import "./resources/notebook_edit_page.css";
import validateUrl from "../common/validator/UrlValidator";

const NotebookEditListItemPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { listItemId } = useParams();

    const [parentId, setParentId] = useState(null);
    const [listItemTitle, setListItemTitle] = useState("");
    const [value, setValue] = useState(null);
    const [listItemType, setListItemType] = useState(null);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => loadItemData(), []);

    const loadItemData = () => {
        const fetch = async () => {
            const response = await Endpoints.NOTEBOOK_GET_LIST_ITEM.createRequest(null, { listItemId: listItemId })
                .send();

            setParentId(response.parentId);
            setListItemTitle(response.title);
            setValue(response.value);
            setListItemType(response.type);
        }
        fetch();
    }

    const save = async () => {
        const listItemTitleResult = validateListItemTitle(listItemTitle);
        if (!listItemTitleResult.valid) {
            NotificationService.showError(listItemTitleResult.message);
            return;
        }

        if (listItemType == ListItemType.LINK) {
            const urlResult = validateUrl(value);
            if (!urlResult.valid) {
                NotificationService.showError(urlResult.message);
                return;
            }
        }

        const payload = {
            parent: parentId,
            title: listItemTitle,
            value: value
        }

        await Endpoints.NOTEBOOK_EDIT_LIST_ITEM.createRequest(payload, { listItemId: listItemId })
            .send();

        window.location.href = Constants.NOTEBOOK_PAGE;
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
                    />

                    {listItemType !== null && listItemType === ListItemType.LINK &&
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
                    leftButtons={
                        <Button
                            id="notebook-edit-back-button"
                            onclick={() => window.location.href = Constants.NOTEBOOK_PAGE}
                            label={localizationHandler.get("back")}
                        />
                    }
                    centerButtons={
                        <Button
                            id="notebook-edit-save-button"
                            onclick={() => save()}
                            label={localizationHandler.get("save")}
                        />
                    }
                />
            </div>

            <ToastContainer />
        </div>
    );
}

export default NotebookEditListItemPage;