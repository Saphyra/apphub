import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./new_link_localization.json";
import "./new_link.css";
import { useParams } from "react-router";
import { useEffect, useState } from "react";
import sessionChecker from "common/js/SessionChecker";
import NotificationService from "common/js/notification/NotificationService";
import ListItemTitle from "../../common/list_item_title/ListItemTitle";
import Header from "common/component/Header";
import ParentSelector from "../../common/parent_selector/ParentSelector";
import InputField from "common/component/input/InputField";
import Footer from "common/component/Footer";
import Button from "common/component/input/Button";
import Constants from "common/js/Constants";
import create from "./NewLinkSaver";
import { ToastContainer } from "react-toastify";
import { NOTEBOOK_NEW_PAGE, NOTEBOOK_PAGE } from "../../NotebookEndpoints";

const NewLinkPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);

    const [listItemTitle, setListItemTitle] = useState("");
    const [url, setUrl] = useState("");

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

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
                rightButtons={[
                    <Button
                        key="back-button"
                        id="notebook-new-link-back-button"
                        label={localizationHandler.get("back")}
                        onclick={() => window.location.href = NOTEBOOK_NEW_PAGE.assembleUrl({ parent: parent })}
                    />,
                    <Button
                        key="home-button"
                        id="notebook-new-link-home-button"
                        label={localizationHandler.get("home")}
                        onclick={() => window.location.href = NOTEBOOK_PAGE}
                    />
                ]}

                centerButtons={[
                    <Button
                        key="create-button"
                        id="notebook-new-link-create-button"
                        label={localizationHandler.get("create")}
                        onclick={() => create(listItemTitle, url, parentId)}
                    />
                ]}
            />

            <ToastContainer />
        </div>
    );
}

export default NewLinkPage;