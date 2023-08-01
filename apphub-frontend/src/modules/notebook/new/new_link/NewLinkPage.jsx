import React, { useEffect, useState } from "react";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import localizationData from "./new_link_localization.json";
import { useParams } from "react-router";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Header from "../../../../common/component/Header";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import ListItemTitle from "../../common/list_item_title/ListItemTitle";
import ParentSelector from "../../common/parent_selector/ParentSelector";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";
import Endpoints from "../../../../common/js/dao/dao";
import { ToastContainer } from "react-toastify";
import InputField from "../../../../common/component/input/InputField";
import "./new_link.css";
import validateUrl from "../../common/validator/UrlValidator";

const NewLinkPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);

    const [listItemTitle, setListItemTitle] = useState("");
    const [url, setUrl] = useState("");

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const create = async () => {
        const listItemTitleResult = validateListItemTitle(listItemTitle);
        if (!listItemTitleResult.valid) {
            NotificationService.showError(listItemTitleResult.message);
            return;
        }

        const urlResult = validateUrl(url);
        if (!urlResult.valid) {
            NotificationService.showError(urlResult.message);
            return;
        }

        const payload = {
            parent: parentId,
            title: listItemTitle,
            url: url
        }
        await Endpoints.NOTEBOOK_CREATE_LINK.createRequest(payload)
            .send();

        window.location.href = Constants.NOTEBOOK_PAGE;
    }

    return (
        <div id="notebook-new-link" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main id="notebook-new-link-main">
                <ListItemTitle
                    inputId="notebook-new-link-title"
                    placeholder={localizationHandler.get("link-title")}
                    setListItemTitle={setListItemTitle}
                    value={listItemTitle}
                />

                <ParentSelector
                    parentId={parentId}
                    setParentId={setParentId}
                />

                <div id="notebook-new-link-content-wrapper">
                    <InputField
                        id="notebook-new-link-content"
                        type="text"
                        onchangeCallback={setUrl}
                        placeholder={localizationHandler.get("url")}
                        value={url}
                    />
                </div>
            </main>

            <Footer
                leftButtons={[
                    <Button
                        key="back-button"
                        id="notebook-new-link-back-button"
                        label={localizationHandler.get("back")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_NEW_PAGE + "/" + parent}
                    />,
                    <Button
                        key="home-button"
                        id="notebook-new-link-home-button"
                        label={localizationHandler.get("home")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_PAGE}
                    />
                ]}

                centerButtons={[
                    <Button
                        key="create-button"
                        id="notebook-new-link-create-button"
                        label={localizationHandler.get("create")}
                        onclick={() => create()}
                    />
                ]}
            />

            <ToastContainer />
        </div>
    );
}

export default NewLinkPage;