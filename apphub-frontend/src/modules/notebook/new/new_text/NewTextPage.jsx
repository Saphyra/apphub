import React, { useEffect, useState } from "react";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import localizationData from "./new_text_localization.json";
import { useParams } from "react-router";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Header from "../../../../common/component/Header";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import ListItemTitle from "../../common/list_item_title/ListItemTitle";
import ParentSelector from "../../common/parent_selector/ParentSelector";
import Textarea from "../../../../common/component/input/Textarea";
import "./new_text.css";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";
import { ToastContainer } from "react-toastify";
import create from "./NewTextSaver";

const NewTextPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);

    const [listItemTitle, setListItemTitle] = useState("");
    const [content, setContent] = useState("");

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    return (
        <div id="notebook-new-text" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main id="notebook-new-text-main">
                <ListItemTitle
                    inputId="notebook-new-text-title"
                    placeholder={localizationHandler.get("text-title")}
                    setListItemTitle={setListItemTitle}
                    value={listItemTitle}
                />

                <ParentSelector
                    parentId={parentId}
                    setParentId={setParentId}
                />

                <div id="notebook-new-text-content-wrapper">
                    <Textarea
                        id="notebook-new-text-content"
                        onchangeCallback={setContent}
                        placeholder={localizationHandler.get("content")}
                        value={content}
                        onKeyDownCallback={e => {
                            if (e.key === "Tab") {
                                e.preventDefault();

                                const start = e.target.selectionStart;
                                const end = e.target.selectionEnd;
                                e.target.value = e.target.value.substring(0, start) + "    " + e.target.value.substring(end);
                                e.target.selectionStart = e.target.selectionEnd = start + 4;
                            }
                        }}
                    />
                </div>
            </main>

            <Footer
                rightButtons={[
                    <Button
                        key="back-button"
                        id="notebook-new-text-back-button"
                        label={localizationHandler.get("back")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_NEW_PAGE + "/" + parent}
                    />,
                    <Button
                        key="home-button"
                        id="notebook-new-text-home-button"
                        label={localizationHandler.get("home")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_PAGE}
                    />
                ]}

                centerButtons={[
                    <Button
                        key="create-button"
                        id="notebook-new-text-create-button"
                        label={localizationHandler.get("create")}
                        onclick={() => create(listItemTitle, parentId, content)}
                    />
                ]}
            />

            <ToastContainer />
        </div>
    );
}

export default NewTextPage;